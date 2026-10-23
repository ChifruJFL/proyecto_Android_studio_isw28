cmake_minimum_required(VERSION 3.22.1)
project(geodoom LANGUAGES CXX)

add_library(geodoom SHARED native_bridge.cpp)
target_compile_features(geodoom PRIVATE cxx_std_17)
