package com.sxh.common.exception.file;

import com.sxh.common.exception.BaseException;

/**
 * 文件信息异常类
 * 
 * @author sxh
 */
public class FileException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args)
    {
        super("file", code, args, null);
    }

}
