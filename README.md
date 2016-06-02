# Shaili - Word recognition for android
This repository consists of

* 'app' module - This is an android app demonstrating the word recognition engine. It implements the following
 	* Camera with Feed cropping
 	* Perspective Cropping for warp correction
 	* Binarization using modified adaptive approach
 	* Word localization algorithm
 	* Recognition using Neural network (caffe-android)
 	
* 'fontrender' module - This is a java library module with the following classes
	* CoverageCalculator - Selects list of words from a 'frequency list' for 'required coverage'
	* Renderer - Class that renders an image for a given 'text', 'rotation' and 'font'
	* SaltAndPepper - Adds 'salt and pepper' noise to input image
	* TimingUtil - Class for processing time measurements
	* WikiExtractor - Extracts a 'frequency list' of words from an input directory of exploded Wikimedia database dump file
	* WordGenerator - Class to render all combinations of training images from 'selected list'. It applies 'rotation', 'font variations' and 'salt pepper noise' for every word and renders png images. It also maintains a mapping of 'training images' and 'classes'
	
## Contact
Contact developer at awanishraj.iitm@gmail.com