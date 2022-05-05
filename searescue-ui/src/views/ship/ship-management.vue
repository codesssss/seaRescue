<template>
  <div class="app-container">

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:dict:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:dict:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:dict:remove']"
        >删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="typeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="船只编号" align="center" prop="id"/>
      <el-table-column label="船只名称" align="center" prop="name" :show-overflow-tooltip="true"/>
      <el-table-column label="船只类型" align="center" prop="type" :formatter="shipTypeFormat"/>
      <el-table-column label="最大速度(海里/时)" align="center" prop="maxspeed"/>
      <el-table-column label="最大负载" align="center" prop="capacity"/>
      <el-table-column label="状态" align="center" prop="isbusy" :formatter="statusFormat"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:dict:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:dict:remove']"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改参数配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="船只名称" prop="name" :required="true">
          <el-input v-model="form.name" placeholder="请输入船只名称" />
        </el-form-item>
        <el-form-item label="船只类型" prop="type" :required="true">
          <el-radio-group v-model="form.type">
            <el-radio
              v-for="dict in shipTypeObj"
              :key="dict.code"
              :label="dict.code"
            >{{ dict.info }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="最大速度" prop="maxspeed" :required="true">
          <el-input v-model="form.maxspeed" placeholder="请输入船只类型"/>
        </el-form-item>
        <el-form-item label="最大负载" prop="capacity"  :required="true">
          <el-input v-model="form.capacity" placeholder="请输入最大负载"/>
        </el-form-item>
        <el-form-item label="当前负载" prop="capacity">
          <el-input v-model="form.load" placeholder="请输入当前负载"/>
        </el-form-item>
        <el-form-item label="经度" prop="longitude">
          <el-input v-model="form.longitude" placeholder="请输入经度"/>
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input v-model="form.latitude" placeholder="请输入纬度"/>
        </el-form-item>

        <el-form-item label="状态" prop="isbusy">
          <el-radio-group v-model="form.isbusy">
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
  </div>
</template>

<script>
import {listType, getType, delType, addType, updateType, exportType, clearCache} from "@/api/system/dict/type";
import {addShip, delShip, getShip, listShip, updateShip} from "../../api/system/ship";
import {selectShipLabel, selectShipTypeLabel} from "../../utils/ruoyi";
import {SHIP_STATUS_OBJ, SHIP_TYPE_OBJ} from "../../store/modules/const";


export default {
  name: "ship-management",
  data() {
    return {
      statusObj:SHIP_STATUS_OBJ,
      shipTypeObj:SHIP_TYPE_OBJ,
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
    this.statusOptions = new Map()
    this.statusOptions.set(0, "空闲")
    this.statusOptions.set(1, "忙碌")

  },
  methods: {
    /** 查询字典类型列表 */
    getList() {
      this.loading = true;
      listShip().then(response => {
          this.typeList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    // 字典状态字典翻译
    statusFormat(row, column) {
      return selectShipLabel(row.isbusy);
    },
    shipTypeFormat(row, column) {
      return selectShipTypeLabel(row.type);
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: undefined,
        name: undefined,
        type: undefined,
        capacity: undefined,
        maxspeed: undefined,
        load: undefined,
        isbusy: undefined,
        longitude: undefined,
        latitude: undefined
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
      this.reset();
      this.open = true;
      this.title = "添加船只";
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length != 1
      this.multiple = !selection.length
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const dictId = row.id || this.ids
      getShip(dictId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改船只";
      });
    },
    /** 提交按钮 */
    submitForm: function () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            updateShip(this.form).then(response => {
              this.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addShip(this.form).then(response => {
              this.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const dictIds = row.shipId || this.ids;
      this.$confirm('是否确认删除船只编号为"' + dictIds + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function () {
        return delShip(dictIds);
      }).then(() => {
        this.getList();
        this.msgSuccess("删除成功");
      })
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
