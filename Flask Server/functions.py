from __future__ import division
import cv2
import matplotlib
from matplotlib import colors
from matplotlib import pyplot as plt
import numpy as np

def show(image):
    # Figure size in inches
    plt.figure(figsize=(5, 5))
    
    # Show image, with nearest neighbour interpolation
    plt.imshow(image, interpolation='nearest')
    plt.show(block=False)
    plt.pause(2)
    plt.close('all')

def show_mask(mask):
    plt.figure(figsize=(5, 5))
    plt.imshow(mask, cmap='gray')
    plt.show(block=False)
    plt.pause(2)
    plt.close('all')

def overlay_mask(mask, image):
    plt.figure(figsize=(5, 5))
    rgb_mask = cv2.cvtColor(mask, cv2.COLOR_GRAY2RGB)
    img = cv2.addWeighted(rgb_mask, 0.5, image, 0.5, 0)
    show(img)



def DetectLeaf(image):

	image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
	image = cv2.resize(image,(265,256))
	# Convert from RGB to HSV
	hsv = cv2.cvtColor(image, cv2.COLOR_RGB2HSV)
	# Blur image slightly
	image_blur = cv2.GaussianBlur(image, (7, 7), 0)
	image_blur_hsv = cv2.cvtColor(image_blur, cv2.COLOR_RGB2HSV)

	# 0-10 hue
	min_red = np.array([36,0,0])
	max_red = np.array([86,255,255])
	image_red1 = cv2.inRange(image_blur_hsv, min_red, max_red)

	# 170-180 hue
	min_red2 = np.array([12, 20, 30])
	max_red2 = np.array([86, 100, 100])
	image_red2 = cv2.inRange(image_blur_hsv, min_red2, max_red2)

	# show_mask(image_red1)
	# show_mask(image_red2)
	image_red = image_red1 + image_red2
	show_mask(image_red)

	# Clean up
	kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (15, 15))


	# Fill small gaps
	image_red_closed = cv2.morphologyEx(image_red, cv2.MORPH_CLOSE, kernel)
	show_mask(image_red_closed)

	# Remove specks
	image_red_closed_then_opened = cv2.morphologyEx(image_red_closed, cv2.MORPH_OPEN, kernel)
	show_mask(image_red_closed_then_opened)

	#Find largest contour
	big_contour, red_mask = find_biggest_contour(image_red_closed_then_opened)
	# show_mask(red_mask)

	#Show overlay image
	overlay_mask(red_mask, image)

	# Bounding ellipse
	image_with_ellipse = image.copy()
	ellipse = cv2.fitEllipse(big_contour)
	cv2.ellipse(image_with_ellipse, ellipse, (0,255,0), 2)
	show(image_with_ellipse)

	print("Big contour:", big_contour.size)

	return big_contour.size




def find_biggest_contour(image):
    
    # Copy to prevent modification
    image = image.copy()
    im2,contours, hierarchy = cv2.findContours(image, cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)
    print (len(contours))

    # Isolate largest contour
    contour_sizes = [(cv2.contourArea(contour), contour) for contour in contours]
    biggest_contour = max(contour_sizes, key=lambda x: x[0])[1]
 
    mask = np.zeros(image.shape, np.uint8)
    cv2.drawContours(mask, [biggest_contour], -1, 255, -1)
    return biggest_contour, mask




