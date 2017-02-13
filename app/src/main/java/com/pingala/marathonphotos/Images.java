package com.pingala.marathonphotos;

import java.util.ArrayList;

/**
 * Created by Habeeb on 2/6/2017.
 */

public class Images {

    String img;
    String imgUrl;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Images() {
    }

    public Images(String img,String imgUrl) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
