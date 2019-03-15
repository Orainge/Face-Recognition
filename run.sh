#!/usr/bin/en sh
echo "----------Face Recognition----------"
echo ""
read -p "Please enter the Caffe installation path, press Enter to use the default value(\$CAFFEROOT):" path

if [ "$path" != "" ];
then
	CAFFEROOT=$path
fi

export LD_LIBRARY_PATH=$CAFFEROOT/build/lib/:./extern:$LD_LIBRARY_PATH
echo ""

if [ ! -f "./extern/libPCN.so" ]; then
   echo "------------Compile the Necessary Files------------"
   echo "Start compiling..."

   # Compile java file
   echo ">>>Compiling java file..."
   javac pcn/MainFrame.java extern/ExternModel.java

   # Compile the PCN library file
   echo ">>>Compiling the PCN library file..."
   g++ -fpic -shared -o ./extern/libPCN.so ./extern/compile/PCN.cpp -O3 -D CPU_ONLY -I $CAFFEROOT/include/ -I $CAFFEROOT/.build_release/src/ -L $CAFFEROOT/build/lib/ -lcaffe -lglog -lboost_system -lprotobuf `pkg-config --cflags --libs opencv`

   # Compile face recognition plugin module
   echo ">>>Compiling face recognition plugin module..."
   g++ -fPIC -I $JAVA_HOME/include -I $JAVA_HOME/include/linux -shared -o ./extern/libExternModel.so ./extern/compile/ExternModel.cpp ./extern/compile/PCN.h ./extern/libPCN.so -std=c++11 -O3 -D CPU_ONLY -I $CAFFEROOT/include/ -I $CAFFEROOT/.build_release/src/ -L $CAFFEROOT/build/lib/ -lcaffe -lglog -lboost_system -lprotobuf `pkg-config --cflags --libs opencv`

   echo "Finish!"
   echo ""
fi

echo ">>>Start program"
java pcn.MainFrame
echo ">>>Exit"