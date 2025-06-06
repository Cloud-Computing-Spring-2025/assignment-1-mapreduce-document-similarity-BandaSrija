[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-2e0aaae1b6195c2367325f4f02e2d04e9abb55f0b24a779b69b11b9e10269abc.svg)](https://classroom.github.com/online_ide?assignment_repo_id=18028349&assignment_repo_type=AssignmentRepo)
### **📌 Document Similarity Using Hadoop MapReduce**  

#### **Objective**  
The goal of this assignment is to compute the **Jaccard Similarity** between pairs of documents using **MapReduce in Hadoop**. You will implement a MapReduce job that:  
1. Extracts words from multiple text documents.  
2. Identifies which words appear in multiple documents.  
3. Computes the **Jaccard Similarity** between document pairs.  
4. Outputs document pairs with similarity **above 50%**.  

---

### **📥 Example Input**  

You will be given a single text document. Document containes each line which contains document name and several words. Your task is to compute the **Jaccard Similarity** between all pairs of documents based on the set of words they contain.  

#### **Example Documents**  

##### **doc1.txt**  
```
hadoop is a distributed system
```

##### **doc2.txt**  
```
hadoop is used for big data processing
```

##### **doc3.txt**  
```
big data is important for analysis
```

---

# 📏 Jaccard Similarity Calculator

## Overview

The Jaccard Similarity is a statistic used to gauge the similarity and diversity of sample sets. It is defined as the size of the intersection divided by the size of the union of two sets.

## Formula

The Jaccard Similarity between two sets A and B is calculated as:

```
Jaccard Similarity = |A ∩ B| / |A ∪ B|
```

Where:
- `|A ∩ B|` is the number of words common to both documents
- `|A ∪ B|` is the total number of unique words in both documents

## Example Calculation

Consider two documents:
 
**doc1.txt words**: `{hadoop, is, a, distributed, system}`
**doc2.txt words**: `{hadoop, is, used, for, big, data, processing}`

- Common words: `{hadoop, is}`
- Total unique words: `{hadoop, is, a, distributed, system, used, for, big, data, processing}`

Jaccard Similarity calculation:
```
|A ∩ B| = 2 (common words)
|A ∪ B| = 10 (total unique words)

Jaccard Similarity = 2/10 = 0.2 or 20%
```

## Use Cases

Jaccard Similarity is commonly used in:
- Document similarity detection
- Plagiarism checking
- Recommendation systems
- Clustering algorithms

## Implementation Notes

When computing similarity for multiple documents:
- Compare each document pair
- Output pairs with similarity > 50%

### **📤 Expected Output**  

The output should show the Jaccard Similarity between document pairs in the following format:  
```
(doc1, doc2) -> 60%  
(doc2, doc3) -> 50%  
```

---

### **🛠 Environment Setup: Running Hadoop in Docker**  

Since we are using **Docker Compose** to run a Hadoop cluster, follow these steps to set up your environment.  

#### **Step 1: Install Docker & Docker Compose**  
- **Windows**: Install **Docker Desktop** and enable WSL 2 backend.  
- **macOS/Linux**: Install Docker using the official guide: [Docker Installation](https://docs.docker.com/get-docker/)  

#### **Step 2: Start the Hadoop Cluster**  
Navigate to the project directory where `docker-compose.yml` is located and run:  
```sh
docker-compose up -d
```  
This will start the Hadoop NameNode, DataNode, and ResourceManager services.  

#### **Step 3: Access the Hadoop Container**  
Once the cluster is running, enter the **Hadoop master node** container:  
```sh
docker exec -it hadoop-master /bin/bash
```

---

### **📦 Building and Running the MapReduce Job with Maven**  

#### **Step 1: Build the JAR File**  
Ensure Maven is installed, then navigate to your project folder and run:  
```sh
mvn clean package
```  
This will generate a JAR file inside the `target` directory.  

#### **Step 2: Copy the JAR File to the Hadoop Container**  
Move the compiled JAR into the running Hadoop container:  
```sh
docker cp target/similarity.jar hadoop-master:/opt/hadoop-3.2.1/share/hadoop/mapreduce/similarity.jar
```

---

### **📂 Uploading Data to HDFS**  

#### **Step 1: Create an Input Directory in HDFS**  
Inside the Hadoop container, create the directory where input files will be stored:  
```sh
hdfs dfs -mkdir -p /input
```

#### **Step 2: Upload Dataset to HDFS**  
Copy your local dataset into the Hadoop cluster’s HDFS:  
```sh
hdfs dfs -put /path/to/local/input/* /input/
```

---

### **🚀 Running the MapReduce Job**  

Run the Hadoop job using the JAR file inside the container:  
```sh
hadoop jar similarity.jar DocumentSimilarityDriver /input /output_similarity /output_final
```

---

### **📊 Retrieving the Output**  

To view the results stored in HDFS:  
```sh
hdfs dfs -cat /output_final/part-r-00000
```

If you want to download the output to your local machine:  
```sh
hdfs dfs -get /output_final /path/to/local/output
```
---
### **Step-by-Step Instructions for Running the Project**  

1. Start the Docker environment:
   ```sh
   $ docker compose up -d
   ```

2. Build and copy the JAR file to the Hadoop container:
   ```sh
   $ mvn clean package
   $ docker cp target/DocumentSimilarity-0.0.1-SNAPSHOT.jar resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
   ```

3. Copy the dataset into the container:
   ```sh
   $ docker cp dataset/documents.txt resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
   ```

4. Access the Hadoop container:
   ```sh
   $ docker exec -it resourcemanager /bin/bash
   ```

5. Inside the container, navigate to the Hadoop directory:
   ```sh
   root@container:/# cd /opt/hadoop-3.2.1/share/hadoop/mapreduce/
   ```

6. Create an HDFS directory and upload the dataset:
   ```sh
   hadoop fs -mkdir -p /input/dataset
   hadoop fs -put ./documents.txt /input/dataset
   ```

7. Run the MapReduce job:
   ```sh
   hadoop jar /opt/hadoop-3.2.1/share/hadoop/mapreduce/DocumentSimilarity-0.0.1-SNAPSHOT.jar \
   com.example.controller.DocumentSimilarityDriver /input/dataset/documents.txt /output
   ```

8. View the output:
   ```sh
   hadoop fs -cat /output/*
   ```
   **Example Output:**
   ```
   Document3, Document2    Similarity: 0.10
   Document3, Document1    Similarity: 0.20
   Document2, Document1    Similarity: 0.18
   ```

9. Copy the output back to the local environment:
   ```sh
   docker cp resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/output/ \
   /workspaces/assignment-1-mapreduce-document-similarity/output
   ```

### **Challenges Faced and Solutions**

1. **Incorrect Output Format:**
   - **Issue:** The Reducer was emitting raw word-document pairs instead of Jaccard Similarity.
   - **Solution:** Ensured the Reducer correctly calculates and formats the similarity scores.

2. **File Not Found in Container:**
   - **Issue:** Input files were missing due to incorrect volume mounting.
   - **Solution:** Manually copied files and verified the Docker volume configuration.

3. **Debugging MapReduce Logic:**
   - **Issue:** Errors in intermediate steps led to incorrect similarity calculations.
   - **Solution:** Added logging to the Mapper and Reducer to trace intermediate steps and ensure correct data flow.

