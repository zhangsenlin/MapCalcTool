package com.example.qiang.collect;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LiuchunTest {
	
	private static double EARTH_RADIUS = 6378.137;    
	   
    private static double rad(double d) {    
	    return d * Math.PI / 180.0;    
    }
    
    public static double getDistance(double lat1, double lng1, double lat2,    
            double lng2) {    
        double radLat1 = rad(lat1);    
 		double radLat2 = rad(lat2);    
 		double a = radLat1 - radLat2;    
 		double b = rad(lng1) - rad(lng2);    
 		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)    
 		+ Math.cos(radLat1) * Math.cos(radLat2)    
 		* Math.pow(Math.sin(b / 2), 2)));    
 		s = s * EARTH_RADIUS;    
 		s = Math.round(s * 10000d) / 10000d;    
 		s = s*1000;    
 		return s;    
    }
    
    public static double Claute(List<GPSLocation> data){
  	    int length=data.size();
  	    double [] distance_between_point_0=new double[length];
  	    double [] distance_between_point_1=new double[length];
        GPSLocation point_0=data.get(0);
  	    double point0_x=point_0.getLa();
  	    double point0_y=point_0.getLo();
        
        for(int i=1;i<length-1;i++){
            GPSLocation point=data.get(i);
            double dis=getDistance(point0_x,point0_y,point.getLa(), point.getLo());
            distance_between_point_0[i]=dis/(double)1000;//��ֵ
        }
        
        for(int i=1;i<length-2;i++){
            GPSLocation point_1=data.get(i);
            GPSLocation point_2=data.get(i+1);
        	double dis=getDistance(point_1.getLa(),point_1.getLo(), point_2.getLa(),point_2.getLo());
            distance_between_point_1[i]=dis/(double)1000;//��ֵ
        }
        double area_total=0;
        for(int i=1;i<length-2;i++){
            double dis_1=distance_between_point_0[i];
            double dis_2=distance_between_point_0[i+1];
            double dis_3=distance_between_point_1[i];
            System.out.print("dis_1:"+dis_1+" dis_2:"+dis_2+" dis_3:"+dis_3+"\n");
            double s = HaiLun(dis_1, dis_2, dis_3);
            System.out.print(" area:"+s+"\n");
            
            area_total+=s;
        }
        
        System.out.print("total+area:"+area_total+"\n");

        return area_total;
    }
	
    
    public static double HaiLun(double a,double b,double c){

        if(a<=0 ||b<=0||c<=0){
        	return 0;
        }
        
        double a1,b1,c1;
        
        BigDecimal temp1 = new BigDecimal(a+"");
    	a1 = temp1.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
    	
    	BigDecimal temp2 = new BigDecimal(b+"");
    	b1 = temp2.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
    	
    	BigDecimal temp3 = new BigDecimal(c+"");
    	c1 = temp3.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
    	
        double p=(a1+b1+c1)/2.0;
       
        double temp=p*(p-a1)*(p-b1)*(p-c1);
        if(temp<0){

        	return 0;
        }
        temp=Math.sqrt(Math.abs(temp));
        
        
        BigDecimal tem = new BigDecimal(temp+"");
        double s2 = tem.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
       
        return s2;
    }
}
