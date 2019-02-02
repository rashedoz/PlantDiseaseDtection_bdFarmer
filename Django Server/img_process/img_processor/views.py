from django.shortcuts import render,get_object_or_404
from django.http import HttpResponse
import cv2
import numpy as np
from matplotlib import pyplot as plt

# Create your views here.

def index(request):

	img = cv2.imread('N:/Rice Detection/PlantVillage CrodAi-Labeled/PlantVillage-Dataset/raw/color/Potato___healthy/00fc2ee5-729f-4757-8aeb-65c3355874f2___RS_HL 1864.JPG')
	a = img.shape

	return render(request, 'img_processor.html',{'a': a,})

