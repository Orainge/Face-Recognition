#include "PCN.h"
#include "extern_ExternModel.h"

JNIEXPORT void JNICALL Java_extern_ExternModel_picture (JNIEnv *env, jclass c, jstring path)
{
    const char* str = env->GetStringUTFChars(path, NULL);
    if (str == NULL) {
         return;
     } 
    PCN detector("./extern/model/PCN.caffemodel",
                 "./extern/model/PCN-1.prototxt", "./extern/model/PCN-2.prototxt", "./extern/model/PCN-3.prototxt",
                 "./extern/model/PCN-Tracking.caffemodel",
                 "./extern/model/PCN-Tracking.prototxt");


    detector.SetMinFaceSize(20);
    detector.SetImagePyramidScaleFactor(1.414);
    detector.SetDetectionThresh(0.37, 0.43, 0.97);

    detector.SetTrackingPeriod(30);
    detector.SetTrackingThresh(0.95);
    detector.SetVideoSmooth(false);

    cv::Mat img = cv::imread(str);
    cv::TickMeter tm;
    tm.reset();
    tm.start();
    std::vector<Window> faces = detector.Detect(img);
    tm.stop();
    std::cout << "Image Path: " << str << std::endl;
    std::cout << "Time Cost: "<< tm.getTimeMilli() << " ms" << std::endl;
    cv::Mat faceImg;
    for (int j = 0; j < faces.size(); j++)
    {
        cv::Mat tmpFaceImg = CropFace(img, faces[j], 200);
        faceImg = MergeImgs(faceImg, tmpFaceImg);
    }
    cv::imshow("Recognition Result", faceImg);
    for (int j = 0; j < faces.size(); j++)
    {
        DrawFace(img, faces[j]);
    }

    int face = faces.size();
    std::stringstream ss;
    ss << face;
    if (face > 1)
        cv::putText(img, ss.str() + " faces", cv::Point(20, 45), 4, 1, cv::Scalar(0, 0, 125));
    else
	cv::putText(img, ss.str() + " face", cv::Point(20, 45), 4, 1, cv::Scalar(0, 0, 125));

    cv::imshow("Photo Recognition (Press any key to exit)", img);
    cv::waitKey();

    env->ReleaseStringUTFChars(path, str);
    cv::destroyAllWindows();	
}

JNIEXPORT void JNICALL Java_extern_ExternModel_video (JNIEnv *env, jclass c)
{
PCN detector("./extern/model/PCN.caffemodel",
                 "./extern/model/PCN-1.prototxt", "./extern/model/PCN-2.prototxt", "./extern/model/PCN-3.prototxt",
                 "./extern/model/PCN-Tracking.caffemodel",
                 "./extern/model/PCN-Tracking.prototxt");

    detector.SetMinFaceSize(45);
    detector.SetImagePyramidScaleFactor(1.414);
    detector.SetDetectionThresh(0.37, 0.43, 0.97);

    detector.SetTrackingPeriod(20);
    detector.SetTrackingThresh(0.95);
    detector.SetVideoSmooth(true);

    cv::VideoCapture capture(0);
    cv::Mat img;
    cv::TickMeter tm;
    while (1)
    {
        capture >> img;
        tm.reset();
        tm.start();
        std::vector<Window> faces = detector.DetectTrack(img);
        int face = faces.size();
        tm.stop();
        std::stringstream ss;
        ss << face;
	if (face > 1)
        	   cv::putText(img, ss.str() + " faces", cv::Point(20, 45), 4, 1, cv::Scalar(0, 0, 125));
	else
            cv::putText(img, ss.str() + " face", cv::Point(20, 45), 4, 1, cv::Scalar(0, 0, 125));

        for (int i = 0; i < faces.size(); i++)
            DrawFace(img, faces[i]);

        cv::imshow("Video Recognition (Press Q to exit)", img);
        if (cv::waitKey(1) == 'q')
            break;
    }

    capture.release();
    cv::destroyAllWindows();
}