# Flickr App

This is an Android  app which lets you search for images from flickr and show them to you. 

# External Libraries used

Retrofit2 : For sending HTTP requests and receiving response
Android Universal Image Loader : https://github.com/nostra13/Android-Universal-Image-Loader It is used to show images on an image view by fetching it from given URL

# About Code:

I used recycler view with grid manager layout to show many images in a row column fashion. 
I send an HTTP request to the flickr api using method flickr.photos.search which requires an API key provided by flickr and a text input which you want to search.
There is another input i.e. number of images in a row which dynamically changes the number of image that is going to be shown in a row.
After getting image details from the server I used the photo ID to generate a link (image URL) to get image : http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
After getting Image I used Image loader to load image on Image view 
Padding of 10dp around image and height of every image is 100 dp
There is a view more button at the end which is used to load more images from server



