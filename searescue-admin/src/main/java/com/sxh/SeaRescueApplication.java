package com.sxh;

import com.sxh.quartz.task.WarningTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * 启动程序
 * 
 * @author sxh
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class SeaRescueApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(SeaRescueApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  系统启动成功   ლ(´ڡ`ლ)ﾞ  \n" );
    }

}
