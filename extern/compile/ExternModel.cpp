#include "PCN.h"
#include "extern_ExternModel.h"

JNIEXPORT void JNICALL Java_extern_ExternModel_picture (JNIEnv *env, jclass c, jstring path)
{
    const char* str = env->GetStringUTFChars(path, NULL);
    if (str == NULL) {
         return;
     } 
    PCN detector("./extern/model/PCN.caffemodel","./extern/model/PCN-1.prototxt", "./extern/model/PCN-2.prototxt", "./extern/model/PCN-3.prototxt");

    detector.SetMinFaceSize(45);
    detector.SetScoreThresh(0.37, 0.43, 0.95);
    detector.SetImagePyramidScaleFactor(1.414);
    detector.SetVideoSmooth(false);

    cv::Mat img = cv::imread(str);
    cv::TickMeter tm;
    tm.reset();
    tm.start();
    std::vector<Window> faces = detector.DetectFace(img);
    tm.stop();
    std::cout << "图片路径: " << str << std::endl;
    std::cout << "识别消耗时间: "<< tm.getTimeMilli() << " ms" << std::endl;
    cv::Mat faceImg;
    for (int j = 0; j < faces.size(); j++)
    {
        cv::Mat tmpFaceImg = CropFace(img, faces[j], 200);
        faceImg = MergeImgs(faceImg, tmpFaceImg);
    }
    cv::imshow("识别结果", faceImg);
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

    cv::imshow("PCN 照片识别 (按任意键退出)", img);
    cv::waitKey();

    env->ReleaseStringUTFChars(path, str);
    cv::destroyAllWindows();	
}

JNIEXPORT void JNICALL Java_extern_ExternModel_video (JNIEnv *env, jclass c)
{
    PCN detector("./extern/model/PCN.caffemodel","./extern/model/PCN-1.prototxt", "./extern/model/PCN-2.prototxt", "./extern/model/PCN-3.prototxt");
    detector.SetMinFaceSize(45);
    detector.SetScoreThresh(0.37, 0.43, 0.95);
    detector.SetImagePyramidScaleFactor(1.414);
    detector.SetVideoSmooth(true);

    cv::VideoCapture capture(0);
    cv::Mat img;
    cv::TickMeter tm;
    while (1)
    {
        capture >> img;
        tm.reset();
        tm.start();
        std::vector<Window> faces = detector.DetectFace(img);
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

        cv::imshow("PCN 视频识别 (按 Q 键退出)", img);
        if (cv::waitKey(1) == 'q')
            break;
    }

    capture.release();
    cv::destroyAllWindows();
}
