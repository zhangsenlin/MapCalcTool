package com.example.qiang.collect;
import android.location.Location;
import android.widget.ArrayAdapter;
import java.math.BigDecimal;
import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Administrator on 2017/2/24/024.
 */

public class ClaLong {


    int Bnum = 100*999;

    //存储每一个小三角形的面积
    public double [] arrayArea = new double[Bnum];


    //记录每一点的横纵坐标（经纬度）
    double [] la = new double[Bnum];
    double [] lo = new double[Bnum];
    int count=0;

    //两点长度chang
    double [] clc = new double[Bnum];
    //两点长度 duan
    double [] cld = new double[Bnum];
    public double Claute(List<GPSLocation> list){
        for(GPSLocation data : list){
            la[count]=data.getLa();
            lo[count]=data.getLo();
            count++;
        }
        for(int i=1;i<Bnum;i++){
            float[] results=new float[1];
            Location.distanceBetween(la[0],lo[0],Double.parseDouble(la[i]+""), Double.parseDouble(lo[i]+""),results);
            clc[i]=results[0]/1000.0;//赋值
        }
        for(int i=1;i<Bnum-1;i++){
            float[] results=new float[1];
            Location.distanceBetween(la[i],lo[i],Double.parseDouble(la[i+1]+""), Double.parseDouble(lo[i+1]+""),results);
            cld[i]=results[0]/1000.0;//赋值
        }

        double result = 0;
        for(int i=1;i<Bnum-1;i++){
            double s = ClaLong.HaiLun(clc[i], clc[i+1], cld[i]);

            //用数组存储每一个小三角形的面积
            arrayArea[i]=s;
            result+=s;
        }
        return result;
    }

    public  double[] getArray(){
        return arrayArea;
    }
    public static double HaiLun(double a,double b,double c){

        double a1,b1,c1;

        //对每一个值进行保留小数运算
        BigDecimal temp1 = new BigDecimal(a+"");
        a1 = temp1.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();

        BigDecimal temp2 = new BigDecimal(b+"");
        b1 = temp2.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();

        BigDecimal temp3 = new BigDecimal(c+"");
        c1 = temp3.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
        double p=(a1+b1+c1)/2.0;

        //先做开方再乘运算
        double pa,pb,pc,pAver;
        pAver = Math.sqrt(p);
        pa = Math.sqrt(Math.abs(p-a1));
        pb = Math.sqrt(Math.abs(p-b1));
        pc = Math.sqrt(Math.abs(p-c1));

        double tp,ta,tb,tc;
        BigDecimal temp4 = new BigDecimal(pAver+"");
        tp = temp3.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
        BigDecimal temp5 = new BigDecimal(pa+"");
        ta = temp5.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
        BigDecimal temp6 = new BigDecimal(pb+"");
        tb = temp6.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
        BigDecimal temp7 = new BigDecimal(pc+"");
        tc = temp7.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();

        double temp = tp*ta*tb*tc;
        BigDecimal tem = new BigDecimal(temp+"");
        double s2 = tem.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
        return s2;
    }
}
