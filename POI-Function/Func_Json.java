package all;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/*
 * 此程序以poiRange.csv为输入，将结果保存在poiF1,poiF2.Json中
 * 
 * poiF1: 包含class5的六个不同的功能区的poi数据（每个区域只含有对应功能区的poi），包含经纬度和所属功能编号
 * poiF2:  包含class5的六个不同的功能区的poi数据（每个区域含有所有的poi,只不过poi功能号码相同），包含经纬度和所属功能编号
 */
public class Func_Json {
	 public static int CC[][];
	    public static int getFunction(int label){
	    	int num=-1;
	    	int flag=0;
	    	for (int i = 0; i < CC.length; i++) {
	    		if (flag==1) {
					break;
				}
				for (int j = 0; j < CC[0].length; j++) {
					if (CC[i][j]==-1) {
						break;
					}
					else {
						if (label==CC[i][j]) {
							num=i;
							flag=1;
							break;
						}
					}
				}
			}
	    	return num;
	    }
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		String path1 = "poiRange.csv";
	    String path2="poi_label1.txt";
		String path3 = "poiF1.json";
		String path4 = "poiF2.json";
		String[] d1=null;
		String line=null;
		String line2="";
		String line3="";
		int classPoi=6;
		int classLength=260;
		CC=new int[classPoi][classLength];
		for (int i = 0; i < CC.length; i++) {
			for (int j = 0; j < CC[i].length; j++) {
				CC[i][j]=-1;
			}
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(path1), "utf-8"));
		
		BufferedReader br2 = new BufferedReader(new InputStreamReader(
				new FileInputStream(path2), "utf-8"));
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(path3), "utf-8"));
		BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(path4), "utf-8"));
		
		int poi=-1,fun=-1,label=-1;
		for (int i = 0; i < classPoi; i++) {
			line=br2.readLine();
			d1=line.split(" ");
			for (int j = 0; j < d1.length; j++) {
				CC[i][j]=Integer.parseInt(d1[j]);
			}
		}
		int count=0;
		int flag=0;
		while((line=br.readLine())!=null){
			d1=line.split(",");
			poi=Integer.parseInt(d1[3]);
			label=Integer.parseInt(d1[4]);
			if (poi!=-1&&label!=-1) {
				fun=getFunction(label);
				if (fun==poi) {
					line2+="{\"lng\":"+d1[1].substring(1,d1[1].length()-1)+",\"lat\":"
				         +d1[2].substring(1, d1[2].length()-1)+",\"fun\":"+fun+"},";
					bw.write(line2);
			        bw.newLine();
			        bw.flush();
				}
				line3+="{\"lng\":"+d1[1].substring(1,d1[1].length()-1)+",\"lat\":"
				         +d1[2].substring(1, d1[2].length()-1)+",\"fun\":"+fun+"},";

			     bw2.write(line3);
			     bw2.newLine();
			     bw2.flush();
			}
			
			
			poi=-1;
			label=-1;
			line2="";
			line3="";
		}
		line2="]";
		bw.write(line2);
		bw.flush();
		bw2.write(line2);
		bw2.flush();
		
        bw.close();
		bw2.close();
		br.close();
		br2.close();
		System.out.println("Tranform end"); 
	}

}
