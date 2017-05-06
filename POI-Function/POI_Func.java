package all;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/*
 * poi.csv 原始经处理的poi,而且已经加上了区域编号,式例如下图
 * "241076","永生餐饮","121.603927","31.235058","餐饮服务;中餐厅;中餐厅","275"
 * 
 * 将操作合并起来，包括向poi中添加类，保留统计中间结果到result.txt
 * 和计算TF_IDF大小，并最终得到每个区域的功能，
 * 
 * poiRange.csv 提取有用的信息(名称，经度，纬度，poi类别，所属区域)
 * result.txt(各个poi类别的数量，每个区域含有的poi数量)
 * TF_IDF1/2.txt 记录两种IDF算法产生的TF_IDF大小
 * function1/2.txt 记录两种算法分别对应的每种功能区的区域数量
 * 每个功能区包含的区域编号存储在poi_label.txt文件中
 */
public class POI_Func {
	public static String [][] CC;//代表class的数量
    public static String [][] CC2;//代表SecondClass的数量
    public static int ccc=0;
	public static int getPoiClass(String type) {
		int num=-1;
		int flag=0;
		for (int i = 0; i < CC.length; i++) {
			if (flag==1) {
				flag=0;
				break;
			}
			for (int j = 0; j < CC[i].length; j++) {
				if (CC[i][j]==null) {
					break;
				}
				else if(CC[i][j].equals(type)){
					num=i;
					flag=1;
					break;
				}
			}
		} 
		return num;
	}
	public static int getSecondPoi(String type) {
		int num=-1;
		int flag=0;
		for (int i = 0; i < CC2.length; i++) {
			if (flag==1) {
				flag=0;
				break;
			}
			for (int j = 1; j < CC2[i].length; j++) {
				if (CC2[i][j]==null) {
					break;
				}
				else if(CC2[i][j].equals(type)){
					num=Integer.parseInt(CC2[i][0]);
					ccc++;
					flag=1;
					break;
				}
			}
		} 
		return num;
	}
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		
		/*第一步：对原始数据poi.csv添加class.txt 中的poi类别，提取有用信息，
		并将结果保存在poiRange.csv文件中*/
		String path1 = "class5.txt";
		String path2 = "poi.csv";
		String path3 = "poiRange.csv";
		String path4="result.txt";
		int classLength=6;//记录poi类别的数量
		int secondLength=3;//记录第二级目录的分类
		int bigClass=19;//大类的数量
		int secondClass=11;
		int rangeNum=542;
		String[] d1=null;
		String[] d2=null;
		String line=null;
		String line2="";
		
		CC=new String[classLength][bigClass];
		CC2=new String[secondLength][secondClass];
		int countP[]=new int[classLength];//countP[0]-countP[5]分别对应六种类别的poi数量
		int countR[]=new int[rangeNum];//countR[0]-countR[]分别对应落在50个区域的
	
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(path1), "utf-8"));
		
		BufferedReader br2 = new BufferedReader(new InputStreamReader(
				new FileInputStream(path2), "utf-8"));

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(path3), "utf-8"));
		BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(path4), "utf-8"));
		for (int i = 0; i <classLength; i++) {
			line=br.readLine();
			d1=line.split(" ");
			for (int j = 1; j < d1.length; j++) {
				CC[i][j-1]=d1[j];
			}
		}
		for (int i = 0; i < secondLength; i++) {
			line=br.readLine();
			d1=line.split(" ");
			for (int j = 0; j < d1.length; j++) {
				CC2[i][j]=d1[j];
			}
		}
		int poiC=-1;
		int count=0;
		int range=-1;
		while((line=br2.readLine())!=null){
			d1=line.split(",");
			d2=d1[4].split(";");
			//System.out.println(d2[0].substring(1));
			range=Integer.parseInt(d1[5].substring(1, d1[5].length()-1));
			poiC=getPoiClass(d2[0].substring(1));
			if (poiC==-1) {
				//System.out.println(d1[4].substring(1,d1[4].length()-1));
				if (!(d1[4].substring(1,d1[4].length()-1)).equals("NULL")) {
					poiC=getSecondPoi(d2[0].substring(1)+";"+d2[1]);
				}
				if(poiC==-1)
				{
					count++;
				}
			}
			if (range!=-1&&poiC!=-1) {
				 countR[range]++;
			}
            if(poiC!=-1&&range!=-1){
			     countP[poiC]++;
           }
			//统计 姓名+经度+纬度+poi分类(六类其中之一)+所属区域
			line2=d1[1]+","+d1[2]+","+d1[3]+","+poiC+","+range;
			bw.write(line2);
			bw.newLine();
			bw.flush();	
		}
		bw.close();
		br.close();
		br2.close();
		CC=null;	
		System.out.println("在区域外的poi数据:"+count);
		System.out.println("通过第二级目录得到的数据:"+ccc);
		
		System.out.println("----------------------------------------------");
		line="";
		System.out.print("poi:");
		for (int i = 0; i < countP.length; i++) {
			System.out.print(countP[i]+" ");
			line+=countP[i]+" ";
		}
		System.out.println();
		bw2.write(line.trim());
		bw2.newLine();
		bw2.flush();
		
		line="";
		System.out.print("Range:");
		for (int i = 0; i < countR.length; i++) {
			System.out.print(countR[i]+",");
			line+=countR[i]+" ";
		}
		System.out.println();
		bw2.write(line.trim());
		bw2.flush();
		bw2.close();
		
		System.out.println("----------------------------------------------");
		/*第二步：进行TF_IDF的计算，要使用countR[] 和 countP[]中的统计数据
		*/
		 path1 = "poiRange.csv";
		 path2="TF_IDF1.txt";
		 path3="function1.txt";
		 path4="poi_label1.txt";
		d1=null;
		d2=null;
		int R=542;
		int countPoi=0;//统计有效的poi数量
		br = new BufferedReader(new InputStreamReader(
				new FileInputStream(path1), "utf-8"));
		bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(path2), "utf-8"));
		bw2 = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(path3), "utf-8"));
		
	    line="";
		int TF[][]=new int[R][6];//记录各个区域每个POI类别的数量
		int F[]=new int[6];//代表含有poi类别的区域数
		float IDF[][]=new float[R][6];
		float TF_F[][]=new float[R][6];
		float TF_IDF[][]=new float[R][7];
		
		int numFunc[]=new int [6];//与功能划分相关的变量
		String lable[]=new String[6];
		for (int i = 0; i < lable.length; i++) {
			lable[i]="";
		}
		double max=0l;
		double temp=0l;
		int cc=0;//如果为均为0的归属问题
		int cc2=0;//均为0的个数
		
		int flag=0;
		count=0;
		
		
		for (int i = 0; i < TF.length; i++) {
			for (int j = 0; j < TF[1].length; j++) {
				TF[i][j]=0;
				TF_F[i][j]=0;	
			}
		}
		int r=-1,p=-1;
		while ((line = br.readLine()) != null) {//从文件中获得数据
             d1=line.split(",");
			 r=Integer.parseInt(d1[4]);
			 p=Integer.parseInt(d1[3]);
			 if (r!=-1&&p!=-1) {
				TF[r][p]++;
				countPoi++;
			}
			 
		}
		br.close();
		for (int i = 0; i < TF.length; i++) {
			for (int j = 0; j < TF[1].length; j++) {
				if (countR[i]==0) {
					TF_F[i][j]=0l;
				}
				else {
					TF_F[i][j]=TF[i][j]*1.0f/countR[i];
				}
				
			}
		}
		for (int i = 0; i < TF.length; i++) {
			for (int j = 0; j < TF[1].length; j++) {
				if (TF[i][j]!=0) {
					F[j]++;
				}	
			}
		}
	    //第一种类型的TF_IDF
		for (int i = 0; i < IDF.length; i++) {
			for (int j = 0; j < IDF[1].length; j++) {
				IDF[i][j]=(float) Math.log(R*1.0/(F[j]+1));
				if (IDF[i][j]<0) {
					IDF[i][j]=0;
				}
			}
		}
		for (int i = 0; i < IDF.length; i++) {
			for (int j = 0; j < IDF[1].length; j++) {
				TF_IDF[i][j]=TF_F[i][j]*IDF[i][j];
				temp=TF_IDF[i][j];
				  if (temp>max) {
					  flag=j;
					  max=temp;
				   }
			}
			if (max==0l) {
				cc=cc%6;
				numFunc[cc++]++;
				cc2++;
			}
			else {
				numFunc[flag]++;
				lable[flag]+=count+" ";
			}
			max=0l;
			flag=0;
			count++;
			
		}
		line="";
		for (int i = 0; i < TF.length; i++) {
			for (int j = 0; j < TF[1].length; j++) {
				line+=TF_IDF[i][j]+" ";
			}
			bw.write(line.trim());
			bw.newLine();
			bw.flush();
			
			line="";
		}
		bw.close();
		
		System.out.print("各个功能区含有的区域数(第一种)：");
		for (int i = 0; i < numFunc.length; i++) {
			System.out.print(numFunc[i]+" ");
			bw2.write(numFunc[i]+"");
			bw2.newLine();
			bw2.flush();
		}
		System.out.println();
		bw2.close();
		
		bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(path4), "utf-8"));
		for (int i = 0; i < lable.length-1; i++) {
			bw.write(lable[i].trim());
			bw.newLine();
			bw.flush();
		}
		bw.write(lable[lable.length-1].trim());
		bw.flush();		
		bw.close();
		
		//第二种类型的DF_IDF
		
		path2="TF_IDF2.txt";
		path3="function2.txt";
		path4="poi_label2.txt";
		bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(path2), "utf-8"));
		bw2 = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(path3), "utf-8"));
		count=0;
		for (int i = 0; i < lable.length; i++) {
			lable[i]="";
		}
		for (int i = 0; i < numFunc.length; i++) {
			numFunc[i]=0;
		}
		cc=0;
		
		for (int i = 0; i < IDF.length; i++) {
			for (int j = 0; j < IDF[1].length; j++) {
				IDF[i][j]=(float) (100f/Math.log(countP[j]*1.0/TF[i][j])*Math.log(R*1.0/(F[j]+1)));
				if (IDF[i][j]<0) {
					IDF[i][j]=0;
				}
			}
		}
		for (int i = 0; i < IDF.length; i++) {
			for (int j = 0; j < IDF[1].length; j++) {
				TF_IDF[i][j]=TF_F[i][j]*IDF[i][j];
				temp=TF_IDF[i][j];
				  if (temp>max) {
					  flag=j;
					  max=temp;
				   }
			}
			if (max==0l) {
				cc=cc%6;
				numFunc[cc++]++;
				
			}
			else {
				numFunc[flag]++;
				lable[flag]+=count+" ";
			}
			max=0l;
			flag=0;
			count++;
			
		}
		line="";
		for (int i = 0; i < TF.length; i++) {
			for (int j = 0; j < TF[1].length; j++) {
				line+=TF_IDF[i][j]+" ";
			}
			bw.write(line.trim());
			bw.newLine();
			bw.flush();
			
			line="";
		}
		bw.close();
		System.out.print("各个功能区含有的区域数（第二种）：");
		for (int i = 0; i < numFunc.length; i++) {
			System.out.print(numFunc[i]+" ");
			bw2.write(numFunc[i]+"");
			bw2.newLine();
			bw2.flush();
		}
		System.out.println();
		bw2.close();
		bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(path4), "utf-8"));
		for (int i = 0; i < lable.length-1; i++) {
			bw.write(lable[i].trim());
			bw.newLine();
			bw.flush();
		}
		bw.write(lable[lable.length-1].trim());
		bw.flush();
		
		bw.close();
		System.out.println("----------------------------------------------");
		System.out.println("使用到poi数："+countPoi);
		
	}

}
