package com.example.smarttechmardan.gallery10;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static int IMG_RESULT=1;
    ImageView imageViewLoad;
    Button LoadImage;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewLoad=(ImageView)findViewById(R.id.imageView);
        LoadImage=(Button)findViewById(R.id.button);

        imageViewLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,IMG_RESULT);
            }
        });

    }//oncreate end////

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode==RESULT_OK && requestCode==1 && null != data){
            decodeUri(data.getData());
        }
    }

    public void decodeUri(Uri uri){
        ParcelFileDescriptor parcelFD=null;
        try{
            parcelFD=getContentResolver().openFileDescriptor(uri,"r");
            FileDescriptor imageSource=parcelFD.getFileDescriptor();

            //Decode image size
           BitmapFactory.Options o= new BitmapFactory.Options();
            o.inJustDecodeBounds=true;
            BitmapFactory.decodeFileDescriptor(imageSource,null,o);

            //the new size we want to scale to
            final int REQUIRED_SIZE=1024;

            //Find the correct scale value. It should be the power of two
            int width_tmp=o.outWidth,height_tmp=o.outHeight;
            int scale=1;
            while (true){
                if (width_tmp<REQUIRED_SIZE && height_tmp<REQUIRED_SIZE){
                    break;
                }
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }

            //decode with in  sample size
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(imageSource,null,o2);

            imageViewLoad.setImageBitmap(bitmap);

        }catch (FileNotFoundException e){
            //handle error
        }catch (IOException e){
            //handle errors

        }finally {
            if (parcelFD!=null)
                try {
                    parcelFD.close();
                }catch (IOException e){
                    //ignored
                }
        }
    }
}
