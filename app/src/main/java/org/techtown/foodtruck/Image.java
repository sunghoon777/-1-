package org.techtown.foodtruck;
import android.graphics.drawable.Drawable;

//이미지 객체
public class Image {
    Drawable drawable;
    String content;

    public Image() {
    }

    public Image(Drawable drawable, String content) {
        this.drawable = drawable;
        this.content = content;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
