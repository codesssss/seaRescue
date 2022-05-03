package com.sxh.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author sxh
 * @date 2022/04/06 01:21
 **/

public class ModelUtils {
    public static void runModel(){
        Process proc;
        try {
            String[] args=new String[]{"python","路径hei"};
            proc = Runtime.getRuntime().exec("python ");
            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
