package org.asciidoctor.asciidoclet;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

public class DocletOptions {
    public static final String ENCODING = "-encoding";
    public static final String OVERVIEW = "-overview";
    public static final String INCLUDE_BASEDIR = "-include-basedir";
    public static final String ATTRIBUTES = "-attributes";

    private final Optional<File> basedir;
    private final Optional<File> overview;
    private final Charset encoding;
    private final List<String> attributes;

    public static final DocletOptions NONE = new DocletOptions(new String[][]{});

    public DocletOptions(RootDoc rootDoc) {
        this(rootDoc.options());
    }

    public DocletOptions(String[][] options) {
        File basedir = null;
        File overview = null;
        Charset encoding = Charset.defaultCharset();
        ImmutableList.Builder<String> attrs = ImmutableList.builder();
        for (String[] option : options) {
            if (option.length > 0) {
                if (INCLUDE_BASEDIR.equals(option[0])) {
                    basedir = new File(option[1]);
                }
                else if (OVERVIEW.equals(option[0])) {
                    overview = new File(option[1]);
                }
                else if (ENCODING.equals(option[0])) {
                    encoding = Charset.forName(option[1]);
                }
                else if (ATTRIBUTES.equals(option[0])) {
                    attrs.addAll(attributeSplitter.split(option[1]));
                }
            }
        }

        this.basedir = Optional.fromNullable(basedir);
        this.overview = Optional.fromNullable(overview);
        this.encoding = encoding;
        this.attributes = attrs.build();
    }

    public Optional<File> overview() {
        return overview;
    }

    public Optional<File> includeBasedir() {
        return basedir;
    }

    public Charset encoding() {
        return encoding;
    }

    public Iterable<String> attributes() {
        return attributes;
    }

    public static boolean validOptions(String[][] options, DocErrorReporter errorReporter, StandardAdapter standardDoclet) {
        DocletOptions docletOptions = new DocletOptions(options);

        if (!docletOptions.includeBasedir().isPresent()) {
            errorReporter.printWarning(INCLUDE_BASEDIR + " must be present for includes or file reference features.");
        }

        return standardDoclet.validOptions(options, errorReporter);
    }

    public static int optionLength(String option, StandardAdapter standardDoclet) {
        if (INCLUDE_BASEDIR.equals(option)) {
            return 2;
        }
        if (ATTRIBUTES.equals(option)) {
            return 2;
        }
        return standardDoclet.optionLength(option);
    }

    private static final Splitter attributeSplitter = Splitter.onPattern("\\s*;\\s*").omitEmptyStrings().trimResults();
}