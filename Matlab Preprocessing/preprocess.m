
I1 = imread('N:/Rice Detection/CrowdAI (plant disease dataset)/crowdai/c_7/8665d5b5-d53d-4501-a6bb-011e6ee1ed61___RS_GLSp 4651 copy 2.jpg');

I= imread('Rice blast images/rice-blast-leaf-lesions-lsu.jpg');
b1 = imread('Rice blast images\blast-leaf.jpg');
b2 = imread('Rice blast images\blast-leaf-2.jpg');
b3 = imread('Rice blast images\blast-leaf-4.jpg');

I_r = imresize(b2,[256,256]);
figure
subplot(2,1,1)
imshow(b2)
subplot(2,1,2)
imshow(I_r)

%%  Convert to grayscale
    
    igray = rgb2gray(I);
    imshow(igray);
    I= igray;
    
%%  Segment
    
    level = 0.5937;
    figure('Name',"Acquired Image")
    Ithresh = im2bw(igray,level);
    imshowpair(I,Ithresh,'montage')
    
%%  Binary Image
    BW = imbinarize(igray,0.6);
    figure
    imshowpair(I,BW,'montage')

%% Contour Image
    mask = zeros(size(BW));
    mask(25:end-25,25:end-25) = 1;
    figure
    imshow(mask)
    title('Initial Contour Location')
    bw = activecontour(BW,mask,300);
    figure
    imshow(bw)
    title('Segmented Image')
%% Blurr Image using Gausian filter 

Iblur = imgaussfilt(I, 2);
figure('Name',"Blurred")
subplot(1,2,1)
imshow(I)
title('Original Image');
subplot(1,2,2)
imshow(Iblur)
title('Gaussian filtered image, \sigma = 2')


%%  Real life blurred image 
PSF = fspecial('gaussian',7,10);
Blurred = imfilter(I,PSF,'symmetric','conv');
imshow(Blurred)
title('Blurred Image')

%% Restore blurred image with Different PSF(point-spread function) Blind Deconvolution Algorithm

figure
UNDERPSF = ones(size(PSF)-4);
[J1,P1] = deconvblind(Blurred,UNDERPSF);
imshow(J1)
title('Deblurring with Undersized PSF')

figure
OVERPSF = padarray(UNDERPSF,[4 4],'replicate','both');
[J2,P2] = deconvblind(Blurred,OVERPSF);
imshow(J2)
title('Deblurring with Oversized PSF')

figure
INITPSF = padarray(UNDERPSF,[2 2],'replicate','both');
[J3,P3] = deconvblind(Blurred,INITPSF);
imshow(J3)
title('Deblurring with INITPSF')

figure;
subplot(2,2,1)
imshow(PSF,[],'InitialMagnification','fit')
title('True PSF')
subplot(222)
imshow(P1,[],'InitialMagnification','fit')
title('Reconstructed Undersized PSF')
subplot(2,2,3)
imshow(P2,[],'InitialMagnification','fit')
title('Reconstructed Oversized PSF')
subplot(2,2,4)
imshow(P3,[],'InitialMagnification','fit')
title('Reconstructed true PSF')

    
 %% Deconvolution
 
figure;imshow(I);title('Original Image');
text(size(I,2),size(I,1)+15, ...
    'Image courtesy of Massachusetts Institute of Technology', ...
    'FontSize',7,'HorizontalAlignment','right');