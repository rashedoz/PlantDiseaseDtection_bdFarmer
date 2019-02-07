from django.shortcuts import render,get_object_or_404
from django.http import HttpResponse
import cv2
import numpy as np
from matplotlib import pyplot as plt
from PIL import Image
import os
from keras.models import load_model
from keras.preprocessing.image import img_to_array


def index(request):

	CATEGORIES = ['Apple___Apple_scab','Apple___Black_rot','Apple___Cedar_apple_rust',
	 'Apple___healthy','Blueberry___healthy',
	 'Cherry_(including_sour)___Powdery_mildew',
	 'Cherry_(including_sour)___healthy',
	 'Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot',
	 'Corn_(maize)___Common_rust_', 'Corn_(maize)___Northern_Leaf_Blight',
	 'Corn_(maize)___healthy', 'Grape___Black_rot',
	 'Grape___Esca_(Black_Measles)',
	 'Grape___Leaf_blight_(Isariopsis_Leaf_Spot)', 'Grape___healthy',
	 'Orange___Haunglongbing_(Citrus_greening)', 'Peach___Bacterial_spot',
	 'Peach___healthy', 'Pepper,_bell___Bacterial_spot',
	 'Pepper,_bell___healthy', 'Potato___Early_blight', 'Potato___Late_blight',
	 'Potato___healthy', 'Raspberry___healthy', 'Soybean___healthy',
	 'Squash___Powdery_mildew', 'Strawberry___Leaf_scorch',
	 'Strawberry___healthy', 'Tomato___Bacterial_spot', 'Tomato___Early_blight',
	 'Tomato___Late_blight', 'Tomato___Leaf_Mold', 'Tomato___Septoria_leaf_spot',
	 'Tomato___Spider_mites Two-spotted_spider_mite', 'Tomato___Target_Spot',
	 'Tomato___Tomato_Yellow_Leaf_Curl_Virus', 'Tomato___Tomato_mosaic_virus',
	 'Tomato___healthy']


	readDir = 'N:/Rice Detection/PlantVillage CrodAi-Labeled/PlantVillage-Dataset/raw/color/Potato___healthy/'
	writeDir = 'N:/RiceDetectionGithub/RiceDiseaseDtection_bdFarmer/Django Server/img_process/Image Resources/'

	readImage = readDir + '00fc2ee5-729f-4757-8aeb-65c3355874f2___RS_HL 1864.JPG'

	img = cv2.imread(readImage)
	a = img.shape

	# hue sat value -hsv
	hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)

	#Thresholding Segment using hsv
	lower_green = np.array([0,48,0])	#lower value 
	upper_green = np.array([255,255,255])	#upper value

	mask = cv2.inRange(hsv, lower_green, upper_green)
	res = cv2.bitwise_and(img,img, mask= mask)



	write_Orginal = writeDir + 'orginal.jpg'
	write_mask = writeDir + 'mask.jpg'
	write_segmented = writeDir + 'segmented.jpg' 
	cv2.imwrite(write_Orginal,img)
	cv2.imwrite(write_mask,mask)
	cv2.imwrite(write_segmented,res)

	image = Image.open(write_segmented)
	print(type(image))

	model = load_model("N:/RiceDetectionGithub/RiceDiseaseDtection_bdFarmer/Django Server/img_process/tfModels/model1.h5")
	print("MODEL-LOADED")

	IMG_SIZE = 256
	img_array = cv2.imread(write_Orginal) 
	new_array = cv2.resize(img_array, (IMG_SIZE, IMG_SIZE)) #RESIZE MAGES
	img_array = img_to_array(new_array)

	np_image_test = np.array(img_array, dtype=np.float16) / 225.0
	np_image_test.shape

	#Expand dimension to predict the model in keras

	np_image_test = np.expand_dims(np_image_test, axis=0)
	np_image_test.shape

	a = model.predict(np_image_test)

	np.around(a[0], decimals=2)

	a = model.predict_classes(np_image_test)
	
	prediction_result = 'Image prediction - '+CATEGORIES[int(a)]

	print(prediction_result)
	# plt.imshow(new_array)
	# plt.show()
	# img_array


	

	# with open(write_segmented, "rb") as f:
	# 	str1 = base64.b64encode(f.read())
	# 	# print(str1)

	

	#Plotting Images
	# plt.subplot(131),plt.imshow(img,cmap = 'gray'),plt.title("img")
	# plt.xticks([]), plt.yticks([])
	# plt.subplot(132),plt.imshow(mask,cmap = 'gray'),plt.title('mask')
	# plt.xticks([]), plt.yticks([])
	# plt.subplot(133),plt.imshow(res,cmap = 'gray'),plt.title('res')
	# plt.xticks([]), plt.yticks([])
	# plt.show()

	

	return render(request, 'img_processor.html',{'a': a,'f':image,'i':img,"prediction":prediction_result})

