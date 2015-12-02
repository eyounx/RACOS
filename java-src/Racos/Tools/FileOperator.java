/* This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Copyright (C) 2015 Nanjing University, Nanjing, China
 */
 
 /**
 * Class FileOperator
 * @author Yi-Qi Hu
 * @time 2015.11.13
 * @version 2.0
 * This class implement some operations for data file. It is convenient for user to read and wirte data.
 */
package Racos.Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class FileOperator {
	
	/**
	 * Constructors without parameter
	 *
	 */
	public FileOperator(){
	}
	
	/**
	 * Read file, file name and path are in FileName, the default encoding format is UTF-8
	 * 
	 * @param FileName, the file name include path
	 * @return an ArrayList class, the contents of file is in this class by line
	 */
	public ArrayList<String> FileReader(String FileName){
		
		ArrayList<String> al = new ArrayList<String>(); 	            
		
		String str = new String(FileName);
		
		try {
			File file = new File(str);
			if (file.isFile() && file.exists()) { // To determine whether the file exists
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file),"UTF-8");// setting encoding format as utf-8
				BufferedReader br = new BufferedReader(read);
				String temp = null;
				while ((temp = br.readLine()) != null) {
					al.add(temp);					
				}
				read.close();
			} else {
				System.out.println("The file does not exist!");
			}
		} catch (Exception e) {
			System.out.println("Reading file error!");
			e.printStackTrace();
		}
		
		return al;
	}
	
	
	/**
	 * override the function of FileReader() with two parameters
	 * 
	 * @param code , the file encoding format
	 * @param FileName, the file name include path
	 * @return Class ArrayList, the contents of file is in this class by line
	 */
	public ArrayList<String> FileReader(String code, String FileName){
		
		ArrayList<String> al = new ArrayList<String>(); 	            
		
		String str = new String(FileName);
		
		try {
			File file = new File(str);
			if (file.isFile() && file.exists()) { // To determine whether the file exists
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file),code);// setting encoding format
				BufferedReader br = new BufferedReader(read);
				String temp = null;
				while ((temp = br.readLine()) != null) {
					al.add(temp);					
				}
				read.close();
			} else {
				System.out.println("The file does not exist!");
			}
		} catch (Exception e) {
			System.out.println("Reading file error!");
			e.printStackTrace();
		}
		
		return al;
	}
	
	/**
	 * write array into file
	 * 
	 * @param temp, an ArrayList class
	 * @param file, the path and name of the file 
	 * @return if write file successfully return true, else return false
	 */
	public boolean FileWriter(ArrayList<String> temp, String file){
		FileOutputStream out = null;
		String outline = null;
		try {
			out = new FileOutputStream(new File(file));
			OutputStreamWriter outp = new OutputStreamWriter(out);
			BufferedWriter bw = new BufferedWriter(outp);
			StringBuffer buffer = new StringBuffer();
			for (int j = 0; j < temp.size(); j++) {
				buffer.append(temp.get(j)+"\n");
			}
			outline = buffer.toString();
			bw.write(outline);
			bw.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
