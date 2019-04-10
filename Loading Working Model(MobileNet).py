
# coding: utf-8

# In[2]:


from keras.models import load_model


# In[3]:


##Loading Model
model = load_model("/home/mazumder_8100/crowdAi/models/model(MobileNet).hdf5")

print("MODEL-LOADED")


# In[25]:


# class_indices = {'Apple___Apple_scab': 0, 'Apple___Black_rot': 1, 'Apple___Cedar_apple_rust': 2, 'Apple___healthy': 3, 'Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot': 4, 'Corn_(maize)___Common_rust_': 5, 'Corn_(maize)___Northern_Leaf_Blight': 6, 'Corn_(maize)___healthy': 7, 'Grape___Black_rot': 8, 'Grape___Esca_(Black_Measles)': 9, 'Grape___Leaf_blight_(Isariopsis_Leaf_Spot)': 10, 'Grape___healthy': 11, 'NotLeaf': 12, 'Potato___Early_blight': 13, 'Potato___Late_blight': 14, 'Potato___healthy': 15, 'Tomato___Bacterial_spot': 16, 'Tomato___Early_blight': 17, 'Tomato___Late_blight': 18, 'Tomato___Leaf_Mold': 19, 'Tomato___Septoria_leaf_spot': 20, 'Tomato___Spider_mites Two-spotted_spider_mite': 21, 'Tomato___Target_Spot': 22, 'Tomato___Tomato_Yellow_Leaf_Curl_Virus': 23, 'Tomato___Tomato_mosaic_virus': 24, 'Tomato___healthy': 25}
#Create Labels
# labels = (class_indices)
# labels = dict((v,k) for k,v in labels.items())


# In[23]:


#Labels
labels = {0: 'Apple___Apple_scab',
 1: 'Apple___Black_rot',
 2: 'Apple___Cedar_apple_rust',
 3: 'Apple___healthy',
 4: 'Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot',
 5: 'Corn_(maize)___Common_rust_',
 6: 'Corn_(maize)___Northern_Leaf_Blight',
 7: 'Corn_(maize)___healthy',
 8: 'Grape___Black_rot',
 9: 'Grape___Esca_(Black_Measles)',
 10: 'Grape___Leaf_blight_(Isariopsis_Leaf_Spot)',
 11: 'Grape___healthy',
 12: 'NotLeaf',
 13: 'Potato___Early_blight',
 14: 'Potato___Late_blight',
 15: 'Potato___healthy',
 16: 'Tomato___Bacterial_spot',
 17: 'Tomato___Early_blight',
 18: 'Tomato___Late_blight',
 19: 'Tomato___Leaf_Mold',
 20: 'Tomato___Septoria_leaf_spot',
 21: 'Tomato___Spider_mites Two-spotted_spider_mite',
 22: 'Tomato___Target_Spot',
 23: 'Tomato___Tomato_Yellow_Leaf_Curl_Virus',
 24: 'Tomato___Tomato_mosaic_virus',
 25: 'Tomato___healthy'}


# ## Loading Image

# In[10]:


from keras.applications.mobilenet import preprocess_input
from keras.preprocessing import image
import numpy as np
import keras


# In[6]:


#Image Preprocessing
def prepare_image(file):
    img_path = ''
    img = image.load_img(img_path + file, target_size=(224, 224))
    img_array = image.img_to_array(img)
    img_array_expanded_dims = np.expand_dims(img_array, axis=0)
    return keras.applications.mobilenet.preprocess_input(img_array_expanded_dims)


# In[13]:


#Prepare and Predict Image
preprocessed_image = prepare_image("/home/mazumder_8100/crowdAi/CustomDataset/Custom-Train-Test(color)/Test/Apple___Apple_scab/08c42d78-aa7b-4106-b0c1-b260f898dcba___FREC_Scab 3151.JPG")
predictions = model.predict(preprocessed_image)


# In[14]:


#Predicted class indicies
predicted_class_indices=np.argmax(predictions,axis=1)
predicted_class_indices


# In[24]:


labels[int(predicted_class_indices)]


# In[41]:


def MakePred(image_dir):
    #Prepare and Predict Image
    preprocessed_image = prepare_image(image_dir)
    predictions = model.predict(preprocessed_image)
    
    #Predicted class indicies
    predicted_class_indices=np.argmax(predictions,axis=1)
    predicted_class_indices
    
#     print(labels[int(predicted_class_indices)])
    return str(labels[int(predicted_class_indices)])
    


# In[26]:


import os


# In[27]:


rootDir = '/home/mazumder_8100/crowdAi/CustomDataset/Custom-Train-Test(color)/Test'


# In[50]:


#Testing Images
Test_parent = os.listdir(rootDir)

for test in Test_parent:
    images = os.listdir(rootDir+'/'+test)
    test_images_count = len (images)
    print('Testing-',test, ' Imag =', test_images_count)
    counter = 0
    for img in images:
        img_dir = rootDir + '/' + test + '/' + img
        res = MakePred(img_dir)
        print(res)
        if res == test:
            counter+= 1
    print("Correct Result:",counter)
    
    

