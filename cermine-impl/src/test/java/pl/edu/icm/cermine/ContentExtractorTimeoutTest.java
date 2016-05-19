package pl.edu.icm.cermine;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.tools.timeout.TimeoutException;

/**
 * 
 * @author Mateusz Kobos
 *
 */
public class ContentExtractorTimeoutTest {
    static final private String COMPLEX_PDF_PATH = "/pl/edu/icm/cermine/tools/timeout/complex.pdf";
    static final private String SIMPLE_PDF_PATH = "/pl/edu/icm/cermine/tools/timeout/simple.pdf";
    static final private long ACCEPTABLE_DELAY = 5000;
    
    @Test
    public void testNoTimeout() 
            throws IOException, TimeoutException, AnalysisException{
        InputStream in = this.getClass().getResourceAsStream(SIMPLE_PDF_PATH);
        ContentExtractor extractor = new ContentExtractor();
        extractor.setPDF(in);
        extractor.getBxDocument();
    }
    
    @Test
    public void testObjectTimeoutRemoval() 
            throws IOException, TimeoutException, AnalysisException{
        InputStream in = this.getClass().getResourceAsStream(SIMPLE_PDF_PATH);
        ContentExtractor extractor = new ContentExtractor();
        extractor.setPDF(in);
        extractor.setTimeout(0);
        extractor.removeTimeout();
        extractor.getBxDocument();
    }
    
    @Test
    public void testObjectTimeoutSetInConstructor() 
            throws IOException, TimeoutException, AnalysisException{
        InputStream in = this.getClass().getResourceAsStream(COMPLEX_PDF_PATH);
        long start = System.currentTimeMillis();
        try {
            ContentExtractor extractor = new ContentExtractor(1);
            extractor.setPDF(in);
            extractor.getBxDocument();
        } catch (TimeoutException ex) {
            assumeTimeoutWithinTimeBound(start);
            return;
        } finally {
            in.close();
        }
        fail("The processing should have been interrupted by timeout but wasn't");
    }
    
    @Test
    public void testObjectTimeout() 
            throws IOException, TimeoutException, AnalysisException{
        assumeOperationsTimeout(
                new ContentExtractorFactory(){
                    @Override
                    public ContentExtractor create(InputStream document) 
                            throws AnalysisException, IOException{
                        ContentExtractor extractor = new ContentExtractor();
                        extractor.setTimeout(1);
                        extractor.setPDF(document);
                        return extractor;
                    }
                }, Collections.singletonList(
                    new ExtractorOperation(){
                        @Override
                        public void run(ContentExtractor extractor) 
                                throws TimeoutException, AnalysisException{
                            extractor.getBxDocument();
                        }
                    }) 
            );
    }
    
    @Test
    public void testMethodTimeout() 
            throws IOException, TimeoutException, AnalysisException{
        assumeOperationsTimeout(Collections.singletonList(
                new ExtractorOperation(){
                    @Override
                    public void run(ContentExtractor extractor) 
                            throws TimeoutException, AnalysisException{
                        extractor.getBxDocument(1);
                    }
                }
          ));
    }
    
    @Test
    public void testAllExtractionOperationsTimeout() 
            throws AnalysisException, IOException{
        /** The timeout set here is zero to make sure that the methods timeout
         * no matter how short they take to execute. */
        List<? extends ExtractorOperation> list = Arrays.asList(
        new ExtractorOperation(){
            @Override
            public void run(ContentExtractor extractor) 
                    throws TimeoutException, AnalysisException{
                extractor.getBxDocument(0);
            }
        },
        new ExtractorOperation(){
            @Override
            public void run(ContentExtractor extractor) 
                    throws TimeoutException, AnalysisException{
                extractor.getMetadata(0);
            }
        },
        new ExtractorOperation(){
            @Override
            public void run(ContentExtractor extractor) 
                    throws TimeoutException, AnalysisException{
                extractor.getNLMMetadata(0);
            }
        },
        new ExtractorOperation(){
            @Override
            public void run(ContentExtractor extractor) 
                    throws TimeoutException, AnalysisException{
                extractor.getReferences(0);
            }
        },
        new ExtractorOperation(){
            @Override
            public void run(ContentExtractor extractor) 
                    throws TimeoutException, AnalysisException{
                extractor.getNLMReferences(0);
            }
        },
        new ExtractorOperation(){
            @Override
            public void run(ContentExtractor extractor) 
                    throws TimeoutException, AnalysisException{
                extractor.getCitationPositions(0);
            }
        },
        new ExtractorOperation(){
            @Override
            public void run(ContentExtractor extractor) 
                    throws TimeoutException, AnalysisException{
                extractor.getCitationSentiments(0);
            }
        },
        new ExtractorOperation(){
            @Override
            public void run(ContentExtractor extractor) 
                    throws TimeoutException, AnalysisException{
                extractor.getRawFullText(0);
            }
        },
        new ExtractorOperation(){
            @Override
            public void run(ContentExtractor extractor) 
                    throws TimeoutException, AnalysisException{
                extractor.getLabelledRawFullText(0);
            }
        },
        new ExtractorOperation(){
            @Override
            public void run(ContentExtractor extractor) 
                    throws TimeoutException, AnalysisException{
                extractor.getNLMText(0);
            }
        },
        new ExtractorOperation(){
            @Override
            public void run(ContentExtractor extractor) 
                    throws TimeoutException, AnalysisException{
                extractor.getNLMContent(0);
            }
        });
        assumeOperationsTimeout(list);
    }
    
    @Test
    public void testObjectAndMethodTimeoutCombinedWithObjectTimeoutActive() 
            throws IOException, TimeoutException, AnalysisException{
        assumeOperationsTimeout(
            new ContentExtractorFactory(){
                @Override
                public ContentExtractor create(InputStream document) 
                        throws AnalysisException, IOException{
                    ContentExtractor extractor = new ContentExtractor();
                    extractor.setTimeout(1);
                    extractor.setPDF(document);
                    return extractor;
                }
            }, Collections.singletonList(
                new ExtractorOperation(){
                    @Override
                    public void run(ContentExtractor extractor) 
                            throws TimeoutException, AnalysisException{
                        extractor.getBxDocument(100);
                    }
                })
        );
    }

    @Test
    public void testObjectAndMethodTimeoutCombinedWithMethodTimeoutActive() 
            throws IOException, TimeoutException, AnalysisException{
        assumeOperationsTimeout(
                new ContentExtractorFactory(){
                    @Override
                    public ContentExtractor create(InputStream document) 
                            throws AnalysisException, IOException{
                        ContentExtractor extractor = new ContentExtractor();
                        extractor.setTimeout(100);
                        extractor.setPDF(document);
                        return extractor;
                    }
                }, Collections.singletonList(
                new ExtractorOperation(){
                    @Override
                    public void run(ContentExtractor extractor) 
                            throws TimeoutException, AnalysisException{
                        extractor.getBxDocument(1);
                    }
                })
            );
    }
    
    private static void assumeOperationsTimeout(
            Collection<? extends ExtractorOperation> operations) 
            throws AnalysisException, IOException{
        assumeOperationsTimeout(new ContentExtractorFactory(){
            @Override
            public ContentExtractor create(InputStream document) 
                    throws AnalysisException, IOException{
                ContentExtractor extractor = new ContentExtractor();
                extractor.setPDF(document);
                return extractor;
            }
        }, operations);
    }
    
    private static void assumeOperationsTimeout(
            ContentExtractorFactory factory,
            Collection<? extends ExtractorOperation> operations) 
            throws AnalysisException, IOException{
        InputStream in = ContentExtractorTimeoutTest.class.getClass()
                .getResourceAsStream(COMPLEX_PDF_PATH);
        ContentExtractor extractor = factory.create(in);
        for(ExtractorOperation op: operations){
            long start = System.currentTimeMillis();
            try {
                op.run(extractor);
            } catch (TimeoutException ex) {
                assumeTimeoutWithinTimeBound(start);
                return;
            } finally {
                in.close();
            }
            fail("The processing should have been interrupted by timeout but wasn't");
        }
    } 

    private static void assumeTimeoutWithinTimeBound(long startMillis) {
        long endMillis = System.currentTimeMillis();
        long diff = endMillis - startMillis;
        if (diff > ACCEPTABLE_DELAY){
            fail("The processing interrupted by the timeout took "+diff
                 +" milliseconds while it should have taken no more than "
                    +ACCEPTABLE_DELAY+" milliseconds");
        }
    }

}

interface ExtractorOperation {
    void run(ContentExtractor extractor) 
            throws TimeoutException, AnalysisException;
}

interface ContentExtractorFactory {
    ContentExtractor create(InputStream document) 
            throws AnalysisException, IOException;
}
