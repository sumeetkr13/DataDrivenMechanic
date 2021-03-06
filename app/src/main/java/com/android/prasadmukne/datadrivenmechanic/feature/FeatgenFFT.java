package com.android.prasadmukne.datadrivenmechanic.feature;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Collections;

public class FeatgenFFT {
	
	public static double[] Complex_abs(double x[], double y[], double norm){ 
		// Calculates the absolute value of a complex number array 
		// assuming the inputs are the real and the imaginary parts of an array	
		int n = x.length;
		double out[] = new double[n];
		if(n != y.length) {
			return null;
		} else {
			for(int i = 0; i < n; i++) {
				out[i] = Math.hypot(x[i], y[i])/norm;
			}
			return out;
		}
	}
	
	public static double[] Array_ebye(double x[], double y[], double scaleFactor){  
		// Calculated element by element addition with sum scaling
		int n = x.length;
		double out[] = new double[n];
		if(n != y.length) {
			return null;
		} else {
			for(int i = 0; i < n; i++) {
				out[i] = (x[i]+y[i])*scaleFactor;
			}
			return out;
		}
	}
	
	public static ArrayList<Double> readAuidoChannelFile(String filename) throws Exception{
		
		// Declaring variables
		double val = 0;
        File myFile = new File(filename);
        System.out.println("Attempting to read from file in: "+myFile.getAbsolutePath());
        
        // Using Java scanner for reading files
        Scanner read = new Scanner (myFile); 
        // Declaring the delimiter
        read.useDelimiter(",");
        
        // Store the data in an Array List of double values
        ArrayList<Double> myList = new ArrayList<Double>();
        
        while(read.hasNext()){
        		val = Double.parseDouble(read.next());
        		myList.add(val);
        }
        
        read.close();
        
        System.out.println("Size of the read file = " + myList.size());
		
        return myList;
	}
	
	public static double[][] convert1Dto2DArray(double[] monoAudioSig, int sampleLength){
		int m = (int) Math.floor(1.0*monoAudioSig.length/sampleLength);
		double audiosigarr[][] = new double[sampleLength][m];
		int t = 0;
		for(int j = 0; j < m; j++) {
			for(int i = 0; i < sampleLength; i++) {
				audiosigarr[i][j] = monoAudioSig[t];
				t++;
			}
		}
		return audiosigarr;
	}
	
	public static double[] convertoMono(ArrayList<Double> myList1, ArrayList<Double> myList2, int sampleLength, int startedgeLength, int endedgeLength) {
		
		int sizeL = myList1.size();
        int sizeR = myList2.size();
        
        // Read the first m values
        int m = Math.min(sizeL,sizeR);
        
        double LeftChannel[] = new double[m];
        double RightChannel[] = new double[m];
        
        for(int i = 0;i < m; i++) {
        		LeftChannel[i] = (double) myList1.get(i);
        		RightChannel[i] = (double) myList2.get(i);
        }
        
    		double[] monoChannel = new double[m];
    		monoChannel = Array_ebye(LeftChannel,RightChannel,0.5);
    		
    		// Sample sampleLength samples corresponding to 2.5 sec of data
    		// Start from index = startedgeLength+1 to avoid the edge and end at m - endedgeLength
    		if(m < sampleLength + startedgeLength + endedgeLength) {
    			return null;
    		} else {
    			int N = m - (startedgeLength + endedgeLength);
    			double[] monoSampled = new double[N];
    			int j = startedgeLength;
        		for(int i = 0;i<N;i++) {
        			j = startedgeLength + i;
        			monoSampled[i] = monoChannel[j];
        		}
        		System.out.println("Size of the Mono Audio array = " + monoSampled.length);
        		return monoSampled;
    		}
	}
	
	public static double[] detrendArr(double[] y) {
		int n = y.length;
		double[] x = new double[n];
		for(int i = 0;i < n;i++) {
			x[i] = i; 
		}
		double xavg = meanArr(x);
		double yavg = meanArr(y);
		double slope;
		double intercept;
		double num = 0;
		double den = 0;
		for(int i = 0;i < n;i++) {
			num = num + (x[i] - xavg)*(y[i] - yavg);
			den = den + Math.pow((x[i] - xavg),2);
		}
		slope = num/den;
		intercept = yavg - slope*xavg;
		double[] ydet = new double[n];
		for(int i = 0;i < n;i++) {
			ydet[i] = y[i] - intercept - slope*x[i]; 
		}
		return ydet;
	}
	
	public static double meanArr(double[] x) {
		int n = x.length;
		double xavg = 0;
		for(int i = 0;i < n;i++) {
			xavg = xavg + x[i];
		}
		xavg = xavg/n;
		return xavg;
	}
	
	public static double powArr(double[] x) {
		int n = x.length;
		double pow = 0;
		for(int i = 0;i < n;i++) {
			pow = pow + Math.pow(x[i],2);
		}
		return pow;
	}
	
	public static int maxiArr(double[] x, double th) {
		/*
		Assuming a sorted array, find the index that corresponds 
		to the max value of the array which is <= the threshold
		*/  
		int n = x.length;
		int ind = 0;
		if(x[0] < th) {
			for(int i = 0;i < n;i++) {
				if(x[i] > th) {
					ind = i - 1;
					i = n;
				}
			}
		}
		
		return ind;
	}
	
	public static double[][] FFT2DArr(double[][] audiosigarr){
		
		int numcol= audiosigarr[0].length;
		double audiosig[] = new double[audiosigarr.length];
		double audiosigdet[];
		double featFFT2D[][] = new double[audiosigarr.length][audiosigarr[0].length];
		double featFFT[];
        
		for(int i = 0;i<numcol;i++) {
			// System.out.println("At iteration = " + (i+1) + " out of "+ numcol);
			double[] dummyImag = new double[audiosigarr.length]; // need a placeholder imaginary array with all 0s
			for(int j = 0; j < audiosig.length; j ++) {
				audiosig[j] = audiosigarr[j][i];
			}
			audiosigdet = detrendArr(audiosig); // Detrending the audio signal
			double pow = powArr(audiosigdet);
			// System.out.println("Power of the raw signal = " + pow);
			for(int j = 0; j < audiosigdet.length; j ++) {
				audiosigdet[j] = audiosigdet[j]/Math.sqrt(pow); // Power normalizing the detrended signal
			}
			// Using the Fft method to perform FFT on the audio data
			Fft.transform(audiosigdet,dummyImag);
			featFFT = Complex_abs(audiosigdet,dummyImag,audiosigdet.length);
			// System.out.println(Arrays.toString(featFFT));
			// System.out.println("Size of the FFT of the input signal = " + featFFT.length);
			for(int j = 0; j < audiosig.length; j ++) {
				featFFT2D[j][i] = Math.pow(10, 3)*featFFT[j]; //Scaling the FFT features
			}
			// System.out.println("Example order of the FFT feature = " + featFFT2D[audiosig.length-1][i]);
		}
		return featFFT2D;
	}
	
	public static double[][] audFeat(double[][] audiosigarr){
		
		// Calculate features like scaled power, variance, max - min
		
		int m = 3;
		
		int numcol= audiosigarr[0].length;
		double audiosig[] = new double[audiosigarr.length];
		double audiosigdet[];
		double audFeat[][] = new double[m][audiosigarr[0].length];
		
		for(int i = 0;i<numcol;i++) {
			// System.out.println("At iteration = " + (i+1) + " out of "+ numcol);
			for(int j = 0; j < audiosig.length; j ++) {
				audiosig[j] = audiosigarr[j][i];
			}
			audiosigdet = detrendArr(audiosig); // Detrending the audio signal
			double pow = powArr(audiosigdet)/Math.pow(10, 6); // Use a scaling factor
			audFeat[0][i] = pow;
			// System.out.println("Example scaled power of the signal = " + pow);
			audFeat[1][i] = Math.sqrt(varArr(audiosigdet));
			// System.out.println("Example standard dev of the detrended signal = " + audFeat[1][i]);
			//audFeat[2][i] = Arrays.stream(audiosigdet).max().getAsDouble() - Arrays.stream(audiosigdet).min().getAsDouble();
			audFeat[2][i] = getMaxValue(audiosigdet)-getMinValue(audiosigdet);
			// System.out.println("Example max of the detrended signal = " + Arrays.stream(audiosigdet).max().getAsDouble());
			// System.out.println("Example min of the detrended signal = " + Arrays.stream(audiosigdet).min().getAsDouble());
		}
		return audFeat;
	}
	
	public static double varArr(double[] x){
		double var = 0;
		//double m = Arrays.stream(x).average().getAsDouble();
		double m = getAverageValue(x);
		for(int i = 0; i < x.length; i++) {
			var = var + Math.pow(x[i] - m,2);
		}
		var =  var/(x.length-1);
		return var;
	}

	public static void main(String[] args) throws Exception {

		// General variable declaration
		int Fs = 48000; // Sampling frequency
		int centerFs = Fs/2; // For centering FFT
		int sampleLength = 131072; //Length of a sample should be a power of 2 and >= 2.5s
		double sampleDuration = Math.round(100.0*sampleLength/Fs)/100.0; //Time, in seconds, of sample to be used for fingerprint analysis
		System.out.println("Digital signal of " + sampleDuration + "s @ 48kHz is of length = " + sampleLength);
        
        
        // Frequency feature parameters
        double lowerWindowBound = 0; // Set the lower bound for the region of interest, in Hz
        double upperWindowBound = 10000; // Set the upper bound for the region of interest, in Hz
        double binWidthHz = 10; // Set the uniform window size, in Hz
        double startedge = 1.0; //duration of the audio to leave in the start in sec
        double endedge = 1.0; //duration of the audio to leave in the end in sec
        int startedgeLength = (int) Math.round(Fs*startedge);
        int endedgeLength = (int) Math.round(Fs*endedge);
        
        // histBinRanges = (lowerWindowBound:binWidthHz:upperWindowBound); % Works well at sample duration 0.5s
        
        double freqbin = (double) Fs/sampleLength; // Spacing between FFT values in terms of Hz
        // Bin size over which averaging needs to happen for calculating binned-avg FFT feature
		int bindel = (int) Math.floor((sampleLength*binWidthHz)/Fs);

		// One-sided frequency and FFT arrays
        double[] freqs = new double[(int) (sampleLength/2)];
        // Binned mid-point frequency
        double[] binnedfreqs = new double[(int) (sampleLength/(bindel*2))];
       
        // Generating the frequency array
        for(int i = 0; i < freqs.length; i++) {
        		freqs[i] = (double) (1.0*Fs*i)/(2*freqs.length);
        		// System.out.println(freqs[i]);
        }
        System.out.println("Maximum frequency = " + freqs[(freqs.length-1)]);
        
		System.out.println("Current directory is " + System.getProperty("user.dir"));
    	
        // Reading the text files, might need a different IO 
		// Need to make sure that the input files have at  least 5 seconds of data
		String filename1 = "leftChannel.txt";
        String filename2 = "rightChannel.txt";
        
        // Read the data from the text file and store the data in an Array List of double values
        ArrayList<Double> myList1 = readAuidoChannelFile(filename1);
        ArrayList<Double> myList2 = readAuidoChannelFile(filename2);
        
        double[] monoAudioSig = convertoMono(myList1, myList2, sampleLength, startedgeLength, endedgeLength);
		System.out.println("The audio data is a digital signal of length " + monoAudioSig.length);
        
		double audiosigarr[][] = convert1Dto2DArray(monoAudioSig, sampleLength);
		System.out.println("Size of the 2D signal array = "+ audiosigarr.length +"*"+ audiosigarr[0].length);
       
        
		double featFFT2D[][] = FFT2DArr(audiosigarr);
		System.out.println("Size of the FFT 2D array = "+ featFFT2D.length +"*"+ featFFT2D[0].length);
	
		double audFeat[][] = audFeat(audiosigarr);
		System.out.println("The audio features of the digital signal array is of size " + audFeat.length + "*" + audFeat[0].length);
		
		System.out.println("Length of the frequency array = " + freqs.length);
		System.out.println("Length of the binned frequency array = " + binnedfreqs.length);
		
		// Sub-selecting half of the array length representing one sided spectrum 
		double featFFT2Dos[][] = new double[freqs.length][featFFT2D[0].length];
		for(int j = 0; j < featFFT2D[0].length; j++) {
			for(int i = 0; i < freqs.length; i++) {
				featFFT2Dos[i][j] = featFFT2D[i][j] ;
			}
		}
		
		System.out.println("Size of the onesided-FFT 2D array = " + featFFT2Dos.length + "*" + featFFT2Dos[0].length);
		
		
		// Perform binning of frequency array and binned-averaging of FFT
		int halfdel = (int) (bindel/2);
		System.out.println("Center of bin delta = " + halfdel);
		
		double binnedfeatFFT2D[][]  = new double[binnedfreqs.length][featFFT2Dos[0].length];

		for(int l = 0; l < featFFT2Dos[0].length; l++) {
			int k = 0;
			for(int i = 0; i < binnedfreqs.length; i++) {
				if(k+halfdel < freqs.length) {
					binnedfreqs[i] = freqs[k + halfdel];
					double val = 0;
					for(int j = 0; j < bindel; j++) {
						if(k +  j < featFFT2Dos.length) {
							val = val + featFFT2Dos[k+j][l];
						} else {
							j = bindel;
						}
					}
					val = val/bindel;
					binnedfeatFFT2D[i][l] = val;
					k = k + bindel;
				}
				// System.out.println("At frequency = " + binnedfreqs[i] + "Hz, feature value = " + binnedfeatFFT2D[i][l] );
			}
		}

    
		System.out.println("Size of the binned-frequencies = " + binnedfreqs.length);
		System.out.println("Size of the binned-onesided-FFT 2D array of the input signal = " + binnedfeatFFT2D.length + "*" + binnedfeatFFT2D[0].length);
		
		System.out.println("Maximum binned frequency = " + binnedfreqs[(binnedfreqs.length-1)]);
		
		int maxind = maxiArr(binnedfreqs, upperWindowBound);
		System.out.println("Length of the upper bounded binned frequency array = " + (maxind+1));
		// System.out.println("Max frequency = " + binnedfreqs[maxind]);
		
		
		// Final output of the code as a frequency 1D array and a feature 2D array
		double[] binnedfreqsfinal = new double[(maxind+1)];
		double[][] binnedfeatFFT2Dfinal = new double[(maxind+1)][binnedfeatFFT2D[0].length];
		
		for(int i = 0; i <= maxind; i++) {
			binnedfreqsfinal[i] = binnedfreqs[i]; 
		}
		System.out.println("Max frequency = " + binnedfreqsfinal[(binnedfreqsfinal.length-1)]);
		
		for(int j = 0; j < binnedfeatFFT2D[0].length; j++) {
			for(int i = 0; i <= maxind; i++) {
				binnedfeatFFT2Dfinal[i][j] = binnedfeatFFT2D[i][j];
			}
		}
		
		System.out.println("Length of the final frequency array = " + binnedfreqsfinal.length + " with the max frequency = " + binnedfreqsfinal[(maxind-1)]);
		System.out.println("Size of the binned-onesided-fequencycapped-FFT 2D array of the input signal = " + binnedfeatFFT2Dfinal.length + "*" + binnedfeatFFT2Dfinal[0].length);
		
		
		double[][] featfinal = new double[(binnedfeatFFT2Dfinal.length+3)][binnedfeatFFT2Dfinal[0].length];
		
		for(int j = 0; j < binnedfeatFFT2Dfinal[0].length; j++) {
			for(int i = 0; i < binnedfeatFFT2Dfinal.length; i++) {
				featfinal[i][j] = binnedfeatFFT2Dfinal[i][j];
			}
			featfinal[binnedfeatFFT2Dfinal.length][j] = audFeat[0][j];
			featfinal[binnedfeatFFT2Dfinal.length+1][j] = audFeat[1][j];
			featfinal[binnedfeatFFT2Dfinal.length+2][j] = audFeat[2][j];
		}
		
		System.out.println("Size of the binned-onesided-fequencycapped-FFT 2D array + audio features of the input signal = " + featfinal.length + "*" + featfinal[0].length);

	}


	// getting the maximum value
	public static double getMaxValue(double[] array) {
		double maxValue = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] > maxValue) {
				maxValue = array[i];
			}
		}
		return maxValue;
	}

	// getting the minimum value
	public static double getMinValue(double[] array) {
		double minValue = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] < minValue) {
				minValue = array[i];
			}
		}
		return minValue;
	}

	// getting the average value
	public static double getAverageValue(double [] array)
	{
		double sum = 0;
		for(int i=0; i < array.length ; i++)
			sum = sum + array[i];

		double average = sum / array.length;
		return average;
	}


}
