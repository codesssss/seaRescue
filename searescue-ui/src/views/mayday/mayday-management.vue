<template>
  <div class="app-container">

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['system:dict:add']"
          icon="el-icon-plus"
          size="mini"
          type="primary"
          @click="handleAdd"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['system:dict:edit']"
          :disabled="single"
          icon="el-icon-edit"
          size="mini"
          type="success"
          @click="handleUpdate"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['system:dict:remove']"
          :disabled="multiple"
          icon="el-icon-delete"
          size="mini"
          type="danger"
          @click="handleDelete"
        >删除
        </el-button>
      </el-col>

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="typeList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55"/>
      <el-table-column align="center" label="救援编号" prop="id"/>
      <el-table-column align="center" label="船名" prop="ship.name"/>
      <el-table-column align="center" label="经度" prop="ship.longitude"/>
      <el-table-column align="center" label="纬度" prop="ship.latitude"/>
      <el-table-column align="center" label="搜救船只名" prop="resship.name"/>
      <el-table-column :formatter="statusFormat" align="center" label="状态" prop="status"/>
      <el-table-column align="center" label="附加信息" prop="info"/>
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template slot-scope="scope">
          <el-button
            v-hasPermi="['system:dict:edit']"
            icon="el-icon-edit"
            size="mini"
            type="text"
            @click="handleUpdate(scope.row)"
          >修改
          </el-button>
          <el-button
            v-hasPermi="['system:dict:remove']"
            icon="el-icon-delete"
            size="mini"
            type="text"
            @click="handleDelete(scope.row)"
          >删除
          </el-button>
          <el-button
            v-hasPermi="['system:dict:remove']"
            icon="el-icon-location"
            size="mini"
            type="text"
            @click="handleSend(scope.row)"
          >派遣船只
          </el-button>
          <el-button
            v-hasPermi="['system:dict:remove']"
            icon="el-icon-check"
            size="mini"
            type="text"
            @click="handleFinish(scope.row)"
          >救援完毕
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :limit.sync="queryParams.pageSize"
      :page.sync="queryParams.pageNum"
      :total="total"
      @pagination="getList"
    />

    <!-- 添加或修改参数配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" append-to-body width="500px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item :required="true" label="求救船只" prop="shipid">
          <el-select
            v-model="form.shipid"
            collapse-tags
            placeholder="请选择"
            style="margin-left: 20px;">
            <el-option
              v-for="item in shipList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :required="true" label="附加信息" prop="info">
          <el-input v-model="form.info" placeholder="请输入附加信息"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in statusObj"
              :key="dict.code"
              :label="dict.code"
            >{{ dict.info }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="title" :visible.sync="openSend" append-to-body width="500px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item :required="true" label="救援船只" prop="resship">
          <el-select
            v-model="form.resship"
            collapse-tags
            placeholder="请选择"
            style="margin-left: 20px;">
            <el-option
              v-for="item in form"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitSendForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="title" :visible.sync="openFinish" append-to-body width="500px">

      <el-button type="primary" @click="submitFinishForm">确 定</el-button>
      <el-button @click="cancel">取 消</el-button>

    </el-dialog>

  </div>
</template>

<script>
import {clearCache, exportType} from "@/api/system/dict/type";
import {listShip} from "../../api/system/ship";
import {selectMayDayLabel} from "../../utils/ruoyi";
import {MAYDAY_STATUS_OBJ} from "../../store/modules/const";
import {
  addMayday,
  delMayday,
  finishMayday,
  getMayday,
  getResShip,
  listMayday,
  sendShip,
  updateMayday
} from "../../api/system/mayday";


export default {
  name: "mayday-management",
  data() {
    return {
      statusObj: MAYDAY_STATUS_OBJ,
      shipList: undefined,
      resShipList: undefined,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 字典表格数据
      typeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,

      openSend: false,
      curRow: undefined,

      openFinish: false,
      // 状态数据字典
      statusOptions: [],
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        dictName: undefined,
        dictType: undefined,
        status: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        dictName: [
          {required: true, message: "字典名称不能为空", trigger: "blur"}
        ],
        dictType: [
          {required: true, message: "字典类型不能为空", trigger: "blur"}
        ]
      }
    };
  },
  created() {
    this.getList();
    this.setShip();
  },
  methods: {
    /** 查询字典类型列表 */
    getList() {
      this.loading = true;
      listMayday().then(response => {
          this.typeList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    // 字典状态字典翻译
    statusFormat(row, column) {
      return selectMayDayLabel(row.status);
    },
    shipFormat(row, column) {
      for (let ship in this.shipList) {
        if (ship.id == row.shipid) {
          return ship.name
        }
      }
      return ' '
    },
    // 取消按钮
    cancel() {
      if (this.open === true) {
        this.open = false;
      }
      if (this.openSend === true)
        this.openSend = false;
      if (this.openFinish === true)
        this.openFinish = false;
      // this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: undefined,
        shipid: undefined,
        info: undefined,
        status: undefined,
        resship: undefined
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      // this.reset();
      this.open = true;
      this.title = "添加呼救";
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length != 1
      this.multiple = !selection.length
    },
    setShip() {
      listShip().then(response => {
        this.shipList = response.rows;
      })
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const dictId = row.id || this.ids
      getMayday(dictId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改呼救";
      });
    },
    /** 提交按钮 */
    submitForm: function () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            updateMayday(this.form).then(response => {
              this.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addMayday(this.form).then(response => {
              this.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 提交派遣按钮 */
    submitSendForm: function () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.resship != undefined) {
            sendShip({resship: this.form.resship, id: this.curRow.id}).then(response => {
              this.msgSuccess("修改成功");
              this.openSend = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 提交完成按钮*/
    submitFinishForm: function () {
          finishMayday({resship: this.curRow.resship.id, id: this.curRow.id}).then(response => {
            this.msgSuccess("已完成救援");
            this.openFinish = false;
            this.getList();
          });
        }
    ,

    /** 删除按钮操作 */
    handleDelete(row) {
      const dictIds = row.id || this.ids;
      this.$confirm('是否确认删除呼救编号为"' + dictIds + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function () {
        return delMayday(dictIds);
      }).then(() => {
        this.getList();
        this.msgSuccess("删除成功");
      })
    },
    handleSend(row) {
      const dictId = row.id || this.ids;
      getResShip(dictId).then(response => {
        this.form = response.data;
        this.openSend = true;
        this.title = "派出船只";
        this.curRow = row;
      });
    },
    handleFinish(row) {
      const dictId = row.id || this.ids;
      this.curRow=row;
      this.openFinish = true;
      this.title = "结束救援";
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$confirm('是否确认导出所有类型数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function () {
        return exportType(queryParams);
      }).then(response => {
        this.download(response.data);
      })
    },
    /** 清理缓存按钮操作 */
    handleClearCache() {
      clearCache().then(response => {
        this.msgSuccess("清理成功");
      });
    }
  }
};
</script>
