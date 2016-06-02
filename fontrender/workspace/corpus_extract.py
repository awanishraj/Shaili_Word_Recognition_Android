# Script to extract words from the corpus

###########################################
corpus_directory = "corpus_files"
output_file = "hindi_corpus_2.txt"
###########################################

# Imports
import glob, operator
import re
import string

file_list =  glob.glob(corpus_directory+"/*.txt")

wordlist = dict()
fout = open(output_file, "w")

def process_line(line):	
	replace_punctuation = string.maketrans(string.punctuation, ' '*len(string.punctuation))
	line = line.translate(replace_punctuation)
	words = line.split()
	for word in words:
		if word not in wordlist:
			wordlist[word] = 1
		else:
			wordlist[word] = wordlist[word] + 1

for filepath in file_list:
	with open(filepath, "r") as corpus_file:
		for line in corpus_file:
			process_line(line)


sorted_x = sorted(wordlist.items(), key=operator.itemgetter(1), reverse = True)

for i in range(len(wordlist)):
	fout.write(sorted_x[i][0]+"\n")
