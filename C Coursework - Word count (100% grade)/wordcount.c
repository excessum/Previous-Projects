#include <stdlib.h>
#include <string.h>
#include <stdio.h>

/*Error codes
* 1XX: File read error
* 2XX: Invalid argument error
* 3XX: Memory allocation error
*/

/*
Student No: C1221987
Student Name:  Curtis Stokes
Module Code: CM2204
Module Title: Advanced Programming
Coursework Title: C Programming Coursework
Lecturer:  Frank C LangbeinHours Spent on this Exercise: 20
Special Provision:  

*/

/*
All references to structs are via pointers and therefore accessed with ->. 
There are a few assumptions in this code and are listed below:
- ' characters are integral to a word and therefore perserved unlike other special characters
- ' characters at the start of a word are deemed illegal and therefore treated as whitespace
- the hashtable is generated using an average word length of 4, an underestimate, this combined 
      with the extra space and special character's ensure it will never overflow
      
Known Issues - 
HashTable size is currently a bit excessive, which leads to a large memory overhead at the cost of better
performance. Without a resize function this ensures that the program will not hang. 
*/

/*General program flow to help understanding:
NOTE: Every malloc or calloc call is checked for NULL. 
NOTE: Error handling is included using error codes above and printed messages.
READING FILE:
1. Open the file, creating a dynamic array(vector based) and hashtable
2. Read each character into a string until a space, non a-Z or ' character is found, then append a '\0'
3. Call hash_update, if word exists in hashtable, increment occurences and return NULL. If word does 
not exist, add it and return the hash item.
4. If the update method returned a hash item, append it to the dynamic array
5. Reset the string and counter
6. When EOF is reached, free the hashtable and return the array.

SORTING AND OUPUTTING FILE:
1. Copy the dynamic array into a pointer array (usable by qsort() lib function)
2. Use qsort() function with supplied compare command to quicksort through list into ascending order
3. Open new FILE pointer with specified parameter name 
4. Loop through sorted array writing "word,count\n"
5. Close file, print completed, exit with code 0. 
*/ 



/*HashItem : Holds the word and the number of times it has appeared in the document. 1:1 mapping between HashItem:unique word */
typedef struct HashItem {
	char *word;
	int occurences;
} HashItem;

/* Creates and returns a new HashItem struct */
HashItem *item_new(char *word){
	HashItem *item = malloc(sizeof(HashItem));
	
	if(item == NULL){
		printf("Memory allocation failed!");
		exit(301);
	}
	
	item->occurences = 1;
	//Determine length of memory to allocate and then copy the string over to allow dereference of pointer
	int chars = strlen(word);
	item->word = (char*)malloc(1*chars+1);
	if(word == NULL){
		printf("Memory allocation failed!");
		exit(302);
	}
	strcpy(item->word, word);
	return item;
}


//HASHTABLE

/* Hash: Hashtable struct holds all the HashItems and a null if there is no HashItem in the position
   Uses linear probing for collision detections
*/
typedef struct {
    int size;
    HashItem **items;
} Hash;
 
 /* Create and return a new Hash struct */
Hash *hash_new (int size) {
    Hash *hash = (Hash*)malloc(sizeof (Hash));
    if(hash == NULL){
		printf("Memory allocation failed!");
		exit(303);
	}
    hash->size = size;
    
    //Calloc due to wanting the unfilled spots to be null
    hash->items = (HashItem**)calloc(size, sizeof (HashItem*));
    if(hash->items == NULL){
		printf("Memory allocation failed!");
		exit(304);
	}
	return hash;
}

/* Hash function used to assign a integer value to a word. Uses x33 method to hash */
int hash_func(char *word, int mod){
	int i = 0;	
	int value = 0;
	while(word[i] != '\0'){
		value = (value*33 + word[i]) % mod;
		i++;
	}
	return value;
}
 
 /* Get the index to place a given word within the passed hash table */
int hash_index (Hash *hash, char *word) {
	//Get initial index for given word
    int index = hash_func(word, hash->size);
    //Get the current item at said position
    HashItem *item = hash->items[index];
    //As long as the item is not null
	while (item){
		//If the item's string is equal to input
		if(!strcmp(item->word, word)){
			//Verified collision found, return the index
			return index;				
		}
		//Else increment the position and get a new element.
	    index = (index + 1) % hash->size;
	    item = hash->items[index];
	}
	//Empty spot found, return the position
    return index;
}

/* Returns the Hashitem that corresponds to the word given, null is returned if it is not found */
HashItem *hash_get (Hash *hash, char *word) {
    int index = hash_index(hash, word);
    return hash->items[index];
}

/* 
Update the passed HashTable with a given word, adds the word if it is not in the table, increments the 
corresponding Hashitem's occurence value if it is. 
*/
HashItem *hash_update (Hash *hash, char *word) {
	//Get the current item (can return null)
    HashItem *item = hash_get(hash, word);
    //If the slot is taken, it is a duplicate value, return null and increment occurence counter of item
	if(item){
		item->occurences++; 	
		return NULL;
	//Else create a  new hash item at the correct position and return said item.
    }else{
    	int index = hash_index(hash, word);
    	item = item_new(word);
    	hash->items[index] = item;
    	return item;
    }
}
 
 //Dynamic Array
 
 /*DynArray is a dynamically resizing array that is used to hold values and retain size data throughout*/
typedef struct{
	int number_of_values; 
	int capacity;
	HashItem **items;
}DynArray;

/*Method to create a new dynamic array and return it */
DynArray* array_new(int file_size){
	DynArray *array = malloc(sizeof(DynArray));
	if(array == NULL){
		printf("Memory allocation failed!");
		exit(304);
	}
	array->number_of_values = 0;
	array->capacity = file_size / 7;
	array->items = malloc(sizeof(HashItem*)* array->capacity);
	if(array->items == NULL){
		printf("Memory allocation failed!");
		exit(305);
	}
	return array;
}

/*Method used to increase the size of the array and reallocate memory*/
void array_increase_if_full(DynArray *array){
	if (array->number_of_values >= array->capacity){
		array->capacity *= 1.25;
		array->items  = realloc(array->items, sizeof(HashItem*)*array->capacity);
		if(array->items == NULL){
			printf("Memory allocation failed!");
		    exit(306);
		}
	}
}

/*Method to add a string to the dynamic array specified */
void array_append(DynArray *array, HashItem *item){
	array_increase_if_full(array);
	array->items[array->number_of_values] = item;
	//printf("item %s added \n at position %d ", array->items[array->number_of_values]->word, array->number_of_values);
	array->number_of_values++;
}

/*Method used to get value at specified position for given array*/
HashItem *array_get(DynArray *array, int position){
	if(position >= array->number_of_values || position <0){
		printf("Index specified out of range");
		exit(202);
	}
	return array->items[position];
}

/* Method used to read a file and get word:count values of different words into a dynamic array and then
return said array. Uses a temporary hash table to collect collisions*/
DynArray *readFile(char* file_path){
	FILE *file;
	file = fopen(file_path, "r");
	if(file == NULL){
		printf("Error opening file %s", file_path);
		exit(101);	
	}
	
	//Work out size of file and create hash table and array accordingly
	fseek(file, 0, SEEK_END);
	int file_size = ftell(file);
	fseek(file, 0, SEEK_SET);
	//Add 100 to the generously overestimated word count to help ensure table is not full
	//even in highly unlikely scenarious like 200 unique 2/3 letter words. 
	Hash* hash = hash_new((file_size/4)*1.25+100);
	DynArray *array = array_new(file_size);
	
	//Will hold current character in input file
	int c;
	//Holds current position in string
	int n = 0;
	//String with memory allocation related to the longest word in english dictionary
	char *string = malloc(45);
	if(string == NULL){
		printf("Memory allocation failed!");
		exit(307);
	}
	//While there is a valid character to read
	while((c = fgetc(file)) != EOF){
		//Check if character is a ' or an alphabetic character
		if ((c > 64 && c < 91) || (c > 96 && c < 123) || c == 39){
			if(c > 64 && c < 91){
				c += 32; //If character is upper case convert to lower
			}
			//A word should never star with ', this accomadates this.
			if(c == 39 && n == 0){
				continue;
			}
			string[n] = c;
			n++;
		}else{
			//Whitespace or invalid special character found. End string
			string[n] = '\0';
			//If the string is not empty
			if(strcmp(string, "")){
				//Call hash_update to either increment the item, or add and retrieve it.
				HashItem *temp_item = hash_update(hash, string); 
				//If update returns a HashItem it already exists therefore add HashItem to array
				if(temp_item != NULL){
					array_append(array, temp_item);
				}
				n = 0;
				//allocate new memory for string
				free(string);
				string = malloc(45);
				if(string == NULL){
					printf("Memory allocation failed!");
					exit(308);
				}
			}
		}
	}
	//Free file and hashtable
	fclose(file);
	free(hash);	
	//Return populated word:count array
	return array;			
}

/* Method used by qsort() to sort the list of HashItems */
int compare_func(const void *a, const void *b)
{
    //Get the HashItem pointer from the HashItem** and cast to correct struct
    const HashItem* aa = *((HashItem**)a);
    const HashItem* bb = *((HashItem**)b);
	
	//Extract the word count of the word corresponding to the item
    int val_1 = aa->occurences;
    int val_2 = bb->occurences;
    
    //If they are equal return 0, if a is bigger return a positive integer, else return a negative. 
    if (val_1 == val_2) {
        return 0;
    } else if (val_1 > val_2) {
        return 1;
    } else {
        return -1;
    }
}

/* Method used to print an array of HashItems to a specified file in word,count\n format */
void print_csv(int size, HashItem **array, char* file_path ){
	FILE *f;
	f = fopen(file_path, "w");
	if(f == NULL){
		printf("Error opening file %s ", file_path);
		exit(102);
	}
	
	int i;
	for(i = 0; i<size; i++){
		fprintf(f, "%s,%d\n" ,array[i]->word, array[i]->occurences);
	}
		
	fclose(f);
}

/*Main method to set everything up and call appriopriate functions in order */
int main (int argc, char *argv[]) {
	if(argc != 3){
		printf("Argument error, usage is : wordcount inputFile outputFile");
		exit(201);
	}
	DynArray *array = readFile(argv[1]);
	printf("Successfully read file \n Now Sorting..... \n");
	int n = array->number_of_values;
	
	HashItem **standard_array = malloc(n*sizeof(HashItem*));
	if(standard_array == NULL){
		printf("Memory allocation failed!");
		exit(309);
	}
	
	int i;
	for(i = 0; i < n; i++){
		standard_array[i] = array_get(array, i);
	}
	free(array);
	
	qsort(standard_array, n, sizeof(HashItem*), compare_func);
	printf("Successfully sorted word list \n Now printing to %s \n", argv[2]);
	print_csv(n, standard_array, argv[2]);
    
	return 0;
}
