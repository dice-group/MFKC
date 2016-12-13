#!/usr/bin/env python
import sparql
import sys
import pickle

reload(sys)
sys.setdefaultencoding("utf-8")

yago = dict()
if len(sys.argv) > 1:
    if sys.argv[1] == "--index":
        print "Indexing Yago..."
        with open('yagoLabels-en.tsv') as f:
            for line in f:
                line = line[:-1].split('\t')
                if line[2] != "rdfs:label":
                    continue
                key = line[1][1:-1]
                val = line[3][1:-5]
                yago[key] = val
        pickle.dump(yago, open("yago.pkl", "wb"))
else:
    print "Loading Yago..."
    yago = pickle.load(open("yago.pkl", "rb"))

ENDPOINT = "http://dbpedia.org/sparql"
STATEMENT = 'SELECT ?x ?l ?y WHERE { ?x a dbo:Person . ?x rdfs:label ?l . ?x owl:sameAs ?y . FILTER(LANG(?l) = "en") . FILTER(REGEX(STR(?y), "http://yago-knowledge.org/resource/")) . } LIMIT 10000 OFFSET 0'

s = sparql.Service(ENDPOINT, "utf-8", "GET")
result = s.query(STATEMENT)

with open('dbpedia_yago_labels_10k.tsv', 'w') as f:
    for row in result.fetchone():
        # row[2] is an IRI, so we use 'unicode' instead of 'str' to show special chars
        uri = unicode(row[2])
        print uri
        # get local name
        loc = uri[35:]
        print "\tn=35, loc={}".format(loc)
        # 'yago' dict expects UTF-8 keys, so we encode the local name
        yl = yago[loc.encode("utf-8")]
        print "\tyl={}".format(yl)
        # save DBpedia label and Yago label
        f.write("{}\t{}\n".format(unicode(row[1]), yl))
