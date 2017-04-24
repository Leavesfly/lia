package com.leavesfly.lia;

import java.io.IOException;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;

import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;

public class DistanceSortingTest {

    public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException,
            IOException {

        RAMDirectory directory = new RAMDirectory();
        IndexWriter indexWriter = new IndexWriter(directory, new WhitespaceAnalyzer(),
                IndexWriter.MaxFieldLength.UNLIMITED);

        addPoint(indexWriter, "El charro", "restaurant", 1, 2);
        addPoint(indexWriter, "Cafe Poca Cosa", "restaurant", 5, 9);
        addPoint(indexWriter, "Los Betos", "restaurant", 9, 6);
        addPoint(indexWriter, "Nico's Toco Shop", "restaurant", 3, 8);
        indexWriter.close();

        Searcher searcher = new IndexSearcher(directory);
        Query query = new TermQuery(new Term("type", "restaurant"));
        Sort sort = new Sort(new SortField("location", new DistanceComparatorSource(10, 10)));
        TopFieldDocs topDocs = searcher.search(query, null, 5, sort);

        ScoreDoc[] docs = topDocs.scoreDocs;
        for (ScoreDoc sorceDoc : docs) {
            Document document = searcher.doc(sorceDoc.doc);
            System.out.println(document.get("name"));
            System.out.println(sorceDoc);
        }

        searcher.close();
    }

    private static void addPoint(IndexWriter writer, String name, String type, int x, int y)
            throws CorruptIndexException, IOException {
        Document document = new Document();
        document.add(new Field("name", name, Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("type", type, Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("location", x + "," + y, Field.Store.YES, Field.Index.NOT_ANALYZED));
        writer.addDocument(document);
    }

}
