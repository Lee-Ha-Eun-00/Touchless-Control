/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.teamSLL.mlkit;

import static android.Manifest.permission_group.CAMERA;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory;
import com.google.android.gms.common.annotation.KeepName;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.face.Face;

import com.teamSLL.mlkit.Interface.EyeRecognition;
import com.teamSLL.mlkit.Interface.HeadRecognition;
import com.teamSLL.mlkit.Interface.MotionRecongition;
import com.teamSLL.mlkit.Interface.MouthRecognition;
import com.teamSLL.mlkit.source.CameraXViewModel;
import com.teamSLL.mlkit.source.GraphicOverlay;
import com.teamSLL.mlkit.source.VisionImageProcessor;
import com.teamSLL.mlkit.facedetector.FaceDetectorProcessor;
import com.teamSLL.mlkit.preference.PreferenceUtils;


import java.util.List;


/** Live preview demo app for ML Kit APIs using CameraX. */
@KeepName
@RequiresApi(VERSION_CODES.LOLLIPOP)
public final class CameraXLivePreviewActivity extends AppCompatActivity {

  private static final String TAG = "CameraXLivePreview";
  private static final String FACE_DETECTION = "Face Detection";
  private static final String STATE_SELECTED_MODEL = "selected_model";

  private PreviewView previewView;
  private GraphicOverlay graphicOverlay;

  @Nullable private ProcessCameraProvider cameraProvider;
  @Nullable private Preview previewUseCase;
  @Nullable private ImageAnalysis analysisUseCase;
  @Nullable private VisionImageProcessor imageProcessor;
  private boolean needUpdateGraphicOverlayImageSourceInfo;

  private String selectedModel = FACE_DETECTION;
  private int lensFacing = CameraSelector.LENS_FACING_BACK;
  private CameraSelector cameraSelector;

  final int MY_PERMISSION_REQUEST_CODE = 100;
  int APIVersion = android.os.Build.VERSION.SDK_INT;

  private boolean checkCAMERAPermission(){
    int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
    return result == PackageManager.PERMISSION_GRANTED;
  }
  private void showMessagePermission(String message, DialogInterface.OnClickListener okListener){
    new AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("허용", okListener)
            .setNegativeButton("거부", null)
            .create()
            .show();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
    switch (requestCode) {
      case MY_PERMISSION_REQUEST_CODE:
        if (grantResults.length > 0) {
          boolean cameraAccepted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
          if (cameraAccepted) {
            cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

            new ViewModelProvider(this, AndroidViewModelFactory.getInstance(getApplication()))
                    .get(CameraXViewModel.class)
                    .getProcessCameraProvider()
                    .observe(
                            this,
                            provider -> {
                              cameraProvider = provider;
                              bindAllCameraUseCases();
                            });
          }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              if (shouldShowRequestPermissionRationale(CAMERA)) {
                showMessagePermission("권한 허가를 요청합니다",
                        new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                              requestPermissions(new String[]{CAMERA}, MY_PERMISSION_REQUEST_CODE);
                            }
                          }
                        });
              }
            }
          }
        }
        break;
    }
  }


  private class WebViewClientClass extends WebViewClient {//페이지 이동
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      Log.d("check URL",url);
      view.loadUrl(url);
      return true;
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");
    setContentView(R.layout.activity_vision_camerax_live_preview);

    WebView webView = (WebView) findViewById(R.id.web);
    webView.loadUrl("https://m.naver.com");
    webView.setWebChromeClient(new WebChromeClient());
    webView.setWebViewClient(new WebViewClientClass());

    previewView = findViewById(R.id.preview_view);
    if (previewView == null) {
      Log.d(TAG, "previewView is null");
    }

    graphicOverlay = findViewById(R.id.graphic_overlay);
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null");
    }
    if (savedInstanceState != null) {
      selectedModel = savedInstanceState.getString(STATE_SELECTED_MODEL, FACE_DETECTION);
    }

    if(APIVersion >= Build.VERSION_CODES.M) {
      if (checkCAMERAPermission()) {
        cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

        new ViewModelProvider(this, AndroidViewModelFactory.getInstance(getApplication()))
                .get(CameraXViewModel.class)
                .getProcessCameraProvider()
                .observe(
                        this,
                        provider -> {
                          cameraProvider = provider;
                          bindAllCameraUseCases();
                        });
      }else{
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST_CODE);
      }
    }

  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle bundle) {
    super.onSaveInstanceState(bundle);
    bundle.putString(STATE_SELECTED_MODEL, selectedModel);
  }

  @Override
  public void onResume() {
    super.onResume();
    bindAllCameraUseCases();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (imageProcessor != null) {
      imageProcessor.stop();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (imageProcessor != null) {
      imageProcessor.stop();
    }
  }

  private void bindAllCameraUseCases() {
    if (cameraProvider != null) {
      // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
      cameraProvider.unbindAll();
      bindPreviewUseCase();
      bindAnalysisUseCase();
    }
  }

  private void bindPreviewUseCase() {
    if (!PreferenceUtils.isCameraLiveViewportEnabled(this)) {
      return;
    }
    if (cameraProvider == null) {
      return;
    }
    if (previewUseCase != null) {
      cameraProvider.unbind(previewUseCase);
    }

    Preview.Builder builder = new Preview.Builder();
    Size targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing);
    if (targetResolution != null) {
      builder.setTargetResolution(targetResolution);
    }
    previewUseCase = builder.build();
    previewUseCase.setSurfaceProvider(previewView.getSurfaceProvider());
    cameraProvider.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector, previewUseCase);
  }

  boolean flag = true;
  private MotionRecongition MR = new MotionRecongition();

  private void event(List<Face> faces){
    if(faces == null || faces.isEmpty()) return;
    Face face = faces.get(0);

    TextView tvxyz = findViewById(R.id.xyz);
    WebView webView = findViewById(R.id.web);
    TextView tvside = findViewById(R.id.side);
    TextView tvside2 = findViewById(R.id.side2);
    TextView tveyeblink = findViewById(R.id.eyeblink);
    TextView tvmouse = findViewById(R.id.mouse);

    final int result = MR.getAllEvent(face);

    if((result & MotionRecongition.HEAD_UP) != 0){
      if(flag){ //연속해서 page up/down이 일어나면 애니메이션이 부자연스럽기에 한번은 무시하도록 함
        flag = false;
        webView.pageUp(false);
      }else{
        flag = true;
      }
      tvside2.setText("UP");
    }else if((result & MotionRecongition.HEAD_DOWN) != 0){
      if(flag){
        flag = false;
        webView.pageDown(false);
      }else{
        flag = true;
      }
      tvside2.setText("DOWN");
    }else{
      tvside2.setText("CENTER");
    }

    if((result & MotionRecongition.HEAD_RIGHT) != 0){
      tvside.setText("RIGHT");
    }else if((result & MotionRecongition.HEAD_LEFT) != 0){
      tvside.setText("LEFT");
    }else{
      tvside.setText("CENTER");
    }

    if((result & MotionRecongition.EYE_CLOSED_SHORT) != 0){
      tveyeblink.setText("EYE CLOSED SHORT");
    }else if((result & MotionRecongition.EYE_CLOSED_LONG) != 0){
      tveyeblink.setText("EYE CLOSED LONG");
    }else{
      tveyeblink.setText("EYE OPEN");
    }

    if((result & MotionRecongition.MOUSE_OPEN) != 0){
      tvmouse.setText("MOUSE OPEN");
    }else{
      tvmouse.setText("MOUSE CLOSED");
    }

    tvxyz.setText(
            face.getHeadEulerAngleX() + " \n"
            + face.getHeadEulerAngleY() + " \n"
            + face.getHeadEulerAngleZ()
    );
  }

  private void bindAnalysisUseCase() {
    if (cameraProvider == null) {
      return;
    }
    if (analysisUseCase != null) {
      cameraProvider.unbind(analysisUseCase);
    }
    if (imageProcessor != null) {
      imageProcessor.stop();
    }

    Log.i(TAG, "Using Face Detector Processor");
    imageProcessor = new FaceDetectorProcessor(this);

    ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
    Size targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing);
    if (targetResolution != null) {
      builder.setTargetResolution(targetResolution);
    }
    analysisUseCase = builder.build();

    needUpdateGraphicOverlayImageSourceInfo = true;
    analysisUseCase.setAnalyzer(
        // imageProcessor.processImageProxy will use another thread to run the detection underneath,
        // thus we can just runs the analyzer itself on main thread.
        ContextCompat.getMainExecutor(this),
        imageProxy -> {
          if (needUpdateGraphicOverlayImageSourceInfo) {
            boolean isImageFlipped = lensFacing == CameraSelector.LENS_FACING_BACK;
            int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
            if (rotationDegrees == 0 || rotationDegrees == 180) {
              graphicOverlay.setImageSourceInfo(
                  imageProxy.getWidth(), imageProxy.getHeight(), isImageFlipped);
            } else {
              graphicOverlay.setImageSourceInfo(
                  imageProxy.getHeight(), imageProxy.getWidth(), isImageFlipped);
            }
            needUpdateGraphicOverlayImageSourceInfo = false;
          }
          try {
            imageProcessor.processImageProxy(imageProxy, graphicOverlay);
            List<Face> faces = ((FaceDetectorProcessor)imageProcessor).getFaces();
            event(faces);

          } catch (MlKitException e) {
            Log.e(TAG, "Failed to process image. Error: " + e.getLocalizedMessage());
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT)
                .show();
          }
        });

    cameraProvider.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector, analysisUseCase);
  }
}
