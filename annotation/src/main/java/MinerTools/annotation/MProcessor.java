package MinerTools.annotation;

import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.*;
import java.util.*;

//@AutoService(Processor.class)
public class MProcessor implements Processor{
    @Override
    public Set<String> getSupportedOptions(){
        return null;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes(){
        return null;
    }

    @Override
    public SourceVersion getSupportedSourceVersion(){
        return null;
    }

    @Override
    public void init(ProcessingEnvironment processingEnv){

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv){
        return false;
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText){
        return null;
    }
}
