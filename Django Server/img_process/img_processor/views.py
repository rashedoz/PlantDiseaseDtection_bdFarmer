from django.shortcuts import render,get_object_or_404
from django.http import HttpResponse
import cv2
import numpy as np
from matplotlib import pyplot as plt
from PIL import Image
# Import
from gcloud import storage
import base64


def index(request):

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
	

	with open(write_segmented, "rb") as f:
		str1 = base64.b64encode(f.read())
		# print(str1)

	# # Initialize
	# client = storage.Client()
	# print("client created")
	# bucket = client.get_bucket('gs://diseasedetect-e39f6.appspot.com/')
	# print("BUCKET created")
	# # # Download
	# # blob = bucket.get_blob('remote/path/to/file.txt')
	# # print blob.download_as_string()

	# # Upload
	# blob2 = bucket.blob('s.jpg')
	# print(type(blob2))
	# print(write_mask)
	# blob2.upload_from_filename(filename=write_mask)

	#Plotting Images
	plt.subplot(131),plt.imshow(img,cmap = 'gray'),plt.title("img")
	plt.xticks([]), plt.yticks([])
	plt.subplot(132),plt.imshow(mask,cmap = 'gray'),plt.title('mask')
	plt.xticks([]), plt.yticks([])
	plt.subplot(133),plt.imshow(res,cmap = 'gray'),plt.title('res')
	plt.xticks([]), plt.yticks([])
	plt.show()

	

	return render(request, 'img_processor.html',{'a': a,'f':image,'i':img,'str':str1})

