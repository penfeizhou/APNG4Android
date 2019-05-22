cmake_minimum_required(VERSION 3.4.1)

include_directories(./)

file(GLOB native_srcs "*.cpp")

add_library(
        gif-decoder
        SHARED
        ${native_srcs}
       )

find_library(log-lib
        log
        android
        stdc++
        atomic
        m)


target_link_libraries(
        gif-decoder
        ${log-lib})