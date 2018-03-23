package com.example.qiang.collect;

import com.amap.api.maps.model.LatLng;

/**
 * Created by Administrator on 2017/3/12/012.
 */
public class CenterLaLo {
    private double MaxLa;
    private double MaxLo;
    private double MinLa;
    private double MinLo;

    public double[] data;
    public CenterLaLo(String [] str){
        data = new double[str.length];
        for(int i = 0; i < data.length; i ++){
            data[i]=Double.parseDouble(str[i]);
        }
        MaxLa = data[0];
        MinLa = data[0];
        MaxLo = data[1];
        MinLo = data[1];
        for(int i=0;i<data.length-1;i=i+2){
            if(data[i]>MaxLa)
                MaxLa=data[i];
            if(data[i]<MinLa)
                MinLa=data[i];
        }
        for(int i=1;i<data.length-1;i=i+2){
            if(data[i]>MaxLo)
                MaxLo=data[i];
            if(data[i]<MinLo)
                MinLo=data[i];
        }
    }
    public LatLng getCenter(){

        LatLng latlng = new LatLng((MaxLa+MinLa)/2.0,(MaxLo+MinLo)/2.0);
        return latlng;
    }
}
