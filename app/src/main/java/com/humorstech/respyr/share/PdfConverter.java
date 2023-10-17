package com.humorstech.respyr.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfConverter {

    public static File convertLinearLayoutToPdf(Context context, LinearLayout linearLayout) throws IOException {
        // Create a new PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(new FileOutputStream(createPdfFile(context))));

        // Create a document
        Document document = new Document(pdf);

        // Convert the LinearLayout to a Bitmap
        Bitmap bitmap = loadBitmapFromView(linearLayout);

        // Add the bitmap to the PDF
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Image image = new Image(ImageDataFactory.create(byteArray));
        document.add(image);

        // Close the document
        document.close();

        // Return the generated PDF file
        return createPdfFile(context);
    }

    private static Bitmap loadBitmapFromView(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }

    private static File createPdfFile(Context context) throws IOException {
        String fileName = "output.pdf";
        File folder = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "PDFs");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return new File(folder, fileName);
    }
}
