package com.newland.cloudtest.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.content.Context;

/**
 * ********************************************************** 内容摘要 ：
 * <p>
 *
 * 作者 ：Administrator 创建时间 ：2012-8-1 下午9:48:44 当前版本号：v1.0 历史记录 : 日期 : 2012-8-1
 * 下午9:48:44 描述 :提供文件地址就能创建文件 和删除 文件
 ***********************************************************
 */
public class FileHelper {

	/**
	 * 
	 * 函数名称 : createFile 功能描述 : 参数及返回值说明：
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * 
	 *             描述 ：传入一个文件就能自动创建文件 --------------------------------------
	 *
	 */
	public static boolean createFile(File file) throws IOException {
		if (!file.exists()) {
			makeDir(file.getParentFile());
		}
		return file.createNewFile();
	}

	public static void makeDir(File dir) {
		if (!dir.getParentFile().exists()) {
			makeDir(dir.getParentFile());
		}
		dir.mkdir();
	}

	// 创建文件目录
	public static boolean createDir(File file) throws IOException {
		if (!file.exists()) {
			makeDir(file.getParentFile());
		}
		return file.mkdir();
	}

	// public static void main(String args[])
	// {
	// String filePath = "C:/temp/a/b/c/d/e/f/g.txt";
	// File file = new File(filePath);
	// try
	// {
	// System.out.println("file.exists()? " + file.exists());
	// boolean created = createFile(file);
	// System.out.println(created ? "File created":
	// "File exists, not created.");
	// System.out.println("file.exists()? " + file.exists());
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	// }

	/**
	 * 根据路径删除指定的目录或文件，无论存在与否
	 *
	 * @param sPath
	 *            要删除的目录或文件
	 * @return 删除成功返回 true，否则返回 false。
	 *         -------------------------------------------
	 */
	public static boolean DeleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 判断目录或文件是否存在
		if (!file.exists()) { // 不存在返回 false
			return flag;
		} else {
			// 判断是否为文件
			if (file.isFile()) { // 为文件时调用删除文件方法
				return deleteFile(sPath);
			} else { // 为目录时调用删除目录方法
				return deleteDirectory(sPath);
			}
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param sPath
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param sPath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String sPath) {

		boolean flag = false;
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}

		if (files.length == 0) {
			return dirFile.delete();
		}

		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * 函数名称 : copyFile 功能描述 : 参数及返回值说明：
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * 
	 *            描述 ：复制文件
	 * @throws IOException
	 *
	 */

	public static void copyFile(File sourceFile, File targetFile) throws IOException {
		BufferedInputStream inBuffer = null;
		BufferedOutputStream outBuffer = null;

		try {
			// 新建文件输入流并对它进行缓冲
			inBuffer = new BufferedInputStream(new FileInputStream(sourceFile));
			outBuffer = new BufferedOutputStream(new FileOutputStream(targetFile));

			// 缓冲数组
			byte[] b = new byte[1024];
			int len;
			while ((len = inBuffer.read(b)) != -1) {
				outBuffer.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuffer.flush();
		} finally {
			// 关闭流
			if (inBuffer != null) {
				inBuffer.close();
				inBuffer = null;
			}

			if (outBuffer != null) {
				outBuffer.close();
				outBuffer = null;
			}
		}

	}

	/**
	 *  复制文件夹
	 * @param sourceDir
	 * @param targetDir
	 * @throws IOException
	 */
	public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
		// 新建目标目录
		(new File(targetDir)).mkdirs();
		// 获取源文件夹当前下的文件或目录
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				File sourceFile = file[i];
				// 目标文件
				File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
				copyFile(sourceFile, targetFile);
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + "/" + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = targetDir + "/" + file[i].getName();
				copyDirectiory(dir1, dir2);
			}
		}
	}

	/**
	 * 
	 * @param srcFileName
	 * @param destFileName
	 * @param srcCoding
	 * @param destCoding
	 * @throws IOException
	 */
	public static void copyFile(File srcFileName, File destFileName, String srcCoding, String destCoding) throws IOException {// 把文件转换为GBK文件
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFileName), srcCoding));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFileName), destCoding));
			char[] cbuf = new char[1024 * 5];
			int len = cbuf.length;
			int off = 0;
			int ret = 0;
			while ((ret = br.read(cbuf, off, len)) > 0) {
				off += ret;
				len -= ret;
			}
			bw.write(cbuf, 0, off);
			bw.flush();
		} finally {
			if (br != null)
				br.close();
			if (bw != null)
				bw.close();
		}
	}

	
	public static void copyBigDataToSD(String strOutFileName,Context context, String path) throws IOException 
    {  
        InputStream myInput;  
        OutputStream myOutput = new FileOutputStream(strOutFileName);  
        myInput = context.getAssets().open(path);  
        byte[] buffer = new byte[1024];  
        int length = myInput.read(buffer);
        while(length > 0)
        {
            myOutput.write(buffer, 0, length); 
            length = myInput.read(buffer);
        }
        
        myOutput.flush();  
        myInput.close();  
        myOutput.close();        
    }  
}
