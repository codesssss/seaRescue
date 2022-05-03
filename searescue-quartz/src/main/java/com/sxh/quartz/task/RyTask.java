package com.sxh.quartz.task;

import org.springframework.stereotype.Component;
import com.sxh.common.utils.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时任务调度测试
 * 
 * @author sxh
 */
@Component
public class RyTask
{
    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i)
    {
        System.out.println(StringUtils.format("执行多参方法： 字符串类型{}，布尔类型{}，长整型{}，浮点型{}，整形{}", s, b, l, d, i));
    }

    public void ryParams(String params)
    {
        System.out.println("执行有参方法：" + params);
    }

    public void ryNoParams()
    {
        System.out.println("执行无参方法");
    }

    public void windowsDump(String dir, String host, String port, String username, String password, String databasename) throws Exception {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        String sqlname = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        File datafile = new File(file + File.separator + sqlname + ".sql");
        if (datafile.exists()) {
            System.out.println(sqlname + "文件名已存在，请更换");
            return;
        }
        //拼接cmd命令  windows下 cmd   Linux下 /bin/sh
        Process exec = Runtime.getRuntime().exec("cmd /c /usr/bin/mysqldump -h" + host + " -P" + port + " -u " + username + " -p" + password + " " + databasename + " > " + datafile);
        if (exec.waitFor() == 0) {
            System.out.println("数据库备份成功,备份路径为：" + datafile);
        }
    }

    public void linuxDump(String dir, String host, String port, String username, String password, String databasename) throws Exception {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        String sqlname = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        File datafile = new File(file + File.separator + sqlname + ".sql");
        if (datafile.exists()) {
            System.out.println(sqlname + "文件名已存在，请更换");
            return;
        }
        //拼接cmd命令  windows下 cmd   Linux下 /bin/sh
        Process exec = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "/usr/bin/mysqldump -h" + host + " -P" + port + " -u " + username + " -p" + password + " " + databasename + " > " + datafile});
        if (exec.waitFor() == 0) {
            System.out.println("数据库备份成功,备份路径为：" + datafile);
        }
    }

}
