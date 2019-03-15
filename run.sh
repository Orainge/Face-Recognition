#!/usr/bin/en sh
echo "----------PCN 人脸识别程序----------"
echo ""
read -p "请输入 caffe 安装路径，回车使用默认值(\$CAFFEROOT):" path

if [ "$path" != "" ];
then
	CAFFEROOT=$path
fi

export LD_LIBRARY_PATH=$CAFFEROOT/build/lib/:./extern:$LD_LIBRARY_PATH
echo ""

if [ ! -f "./extern/libPCN.so" ]; then
   echo "------------编译必须文件------------"
   echo "开始编译..."

   # 编译 java 文件
   echo ">>>编译 java 文件..."
   javac pcn/MainFrame.java extern/ExternModel.java

   # 编译 PCN 库文件
   echo ">>>编译 PCN 库文件..."
   g++ -fpic -shared -o ./extern/libPCN.so ./extern/compile/PCN.cpp -O3 -D CPU_ONLY -I $CAFFEROOT/include/ -I $CAFFEROOT/.build_release/src/ -L $CAFFEROOT/build/lib/ -lcaffe -lglog -lboost_system -lprotobuf `pkg-config --cflags --libs opencv`

   # 编译人脸识别插件模块
   echo ">>>编译人脸识别插件模块..."
   g++ -fPIC -I $JAVA_HOME/include -I $JAVA_HOME/include/linux -shared -o ./extern/libExternModel.so ./extern/compile/ExternModel.cpp ./extern/compile/PCN.h ./extern/libPCN.so -std=c++11 -O3 -D CPU_ONLY -I $CAFFEROOT/include/ -I $CAFFEROOT/.build_release/src/ -L $CAFFEROOT/build/lib/ -lcaffe -lglog -lboost_system -lprotobuf `pkg-config --cflags --libs opencv`

   echo "编译完成!"
   echo ""
fi

echo ">>>开始运行"
java pcn.MainFrame
echo ">>>结束运行"