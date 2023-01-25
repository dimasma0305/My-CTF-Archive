#!/bin/bash

salt="$(echo sO3XIbeW14| base64 | sed s/=//g)";
password_hash="$(rax2 -s 66c074645545781f1064fb7fd1177453db8f0ca2ce58a9d81c04be2e6d3ba2a0d6c032f0fd4ef83f48d74349ec196f4efe37\
| base64 )";
iteration=10000;

echo sha256:$iteration:$salt:$password_hash