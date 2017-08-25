package com.ogangi.barcode.writer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

/**
 * Created by jmtt on 8/24/17.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.BarcodeViewHolder> {
    private List<Barcode> list;

    public ListAdapter(List<Barcode> list) {
        this.list = new ArrayList<>(list);
    }

    public void addBarcode(Barcode barcode){
        if(!list.contains(barcode)){
            list.add(barcode);
        }else{
            int idx = list.indexOf(barcode);
            list.set(idx, barcode);
        }
        notifyDataSetChanged();
    }

    @Override
    public BarcodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.barcode, parent, false);
        return new BarcodeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BarcodeViewHolder holder, int position) {
        Resources resources = holder.view.getContext().getResources();
        Barcode barcode = list.get(position);
        holder.message.setText(barcode.getMessage());
        holder.format.setText(barcode.getFormat());
        holder.altText.setText(barcode.getAltText());
        try {
            // Generamos el bitmap con el tamano minimo
            Bitmap bitmap = encodeAsBitmap(barcode.getMessage(), barcode.getFormat());

            // Generamos la imagen
            BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, bitmap);
            bitmapDrawable.setFilterBitmap(false);
            bitmapDrawable.setAntiAlias(false);

            //setea la imagen al drawable
            holder.render.setImageDrawable(bitmapDrawable);

            //hace resize
            int screenWith = holder.view.getContext().getResources().getDisplayMetrics().widthPixels;
            int screenHeight = holder.view.getContext().getResources().getDisplayMetrics().heightPixels;
            int smallerSide = Math.min(screenWith, screenHeight);
            boolean quadratic = barcode.getFormat().equals("PKBarcodeFormatAztec") || barcode.getFormat().equals("PKBarcodeFormatQR");
            int sqrWidth = (int) (smallerSide * 0.5);
            int regWidth = (int) (smallerSide * 0.75);

            ViewGroup.LayoutParams params = holder.render.getLayoutParams();
            params.width = quadratic ? sqrWidth : regWidth;//ViewGroup.LayoutParams.WRAP_CONTENT;;
            params.height = quadratic ? sqrWidth : ViewGroup.LayoutParams.WRAP_CONTENT;

            Log.d("Dimensiones", "onBindViewHolder: smallerSide: "+smallerSide);
            Log.d("Dimensiones", "onBindViewHolder: quadratic: "+quadratic);

            holder.render.setLayoutParams(params);


        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class BarcodeViewHolder extends RecyclerView.ViewHolder{
        View view;
        CardView wrapper;
        ImageView render;
        TextView altText;
        TextView format;
        TextView message;

        public BarcodeViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            wrapper = (CardView) itemView.findViewById(R.id.card);
            render = (ImageView) itemView.findViewById(R.id.render);
            altText = (TextView) itemView.findViewById(R.id.alt_text);
            format = (TextView) itemView.findViewById(R.id.format);
            message = (TextView) itemView.findViewById(R.id.message);
        }
    }

    private Bitmap encodeAsBitmap(String str, String format) throws WriterException {
        BarcodeFormat barcodeFormat;
        switch (format){
            case "PKBarcodeFormatAztec":
                barcodeFormat = BarcodeFormat.AZTEC;
                break;
            case "PKBarcodeFormatPDF417":
                barcodeFormat = BarcodeFormat.PDF_417;
                break;
            case "PKBarcodeFormatCode128":
                barcodeFormat = BarcodeFormat.CODE_128;
                break;
            case "PKBarcodeFormatQR":
            default:
                barcodeFormat = BarcodeFormat.QR_CODE;
                break;
        }

        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, barcodeFormat, 0, 0, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        // generate an image from the byte matrix
        boolean is1D = result.getHeight() == 1;
        int w = result.getWidth();
        int h = is1D ? w / 5 : result.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                bitmap.setPixel(x, y, result.get(x, is1D ? 0 : y) ? BLACK : WHITE);
            }
        }
        return bitmap;
    }
}
