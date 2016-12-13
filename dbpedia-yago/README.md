# DBpedia-Yago labels

## Download

The dataset can be found at https://s3.eu-central-1.amazonaws.com/tommaso-soru/files/dbpedia_yago_labels_10k.tsv

## Generation

In order to generate the dataset, get Yago labels from the official website.

```
wget http://resources.mpi-inf.mpg.de/yago-naga/yago/download/yago/yagoLabels.tsv.7z
7z x yagoLabels.tsv.7z
```

Then, filter them by language (e.g., English).

```
grep '@eng' yagoLabels.tsv > yagoLabels-en.tsv
```

Finally, call the Python script.

```
python dbpedia_yago.py --index
```