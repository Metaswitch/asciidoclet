package org.asciidoctor;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import org.asciidoctor.asciidoclet.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * = Asciidoclet
 *
 * https://github.com/asciidoctor/asciidoclet[Asciidoclet] is a Javadoc Doclet
 * that uses http://asciidoctor.org[Asciidoctor] (via the
 * https://github.com/asciidoctor/asciidoctor-java-integration[Asciidoctor Java integration])
 * to render http://asciidoc.org[AsciiDoc] markup within Javadoc comments.
 *
 * == Usage
 * 
 * Asciidoclet may be used via a custom doclet in the maven-javadoc-plugin:
 *
 * [source,xml]
 * ----
 * include::pom.xml[tags=pom_include,indent=0]
 * ----
 *
 * <1> Use the `additionalparam` parameter to pass Asciidoclet parameters to javadoc. 
 *     See <<doclet-options>>.
 *
 * <2> The `-overview` option may refer to an Asciidoc file, see <<doclet-options>>.
 * 
 * == Doclet Options
 *
 * include::README.asciidoc[tags=doclet-options]
 *
 * == Examples
 *
 * Custom attributes::
 * `{project_name}` = {project_name}
 * +
 * `{project_desc}` = {project_desc}
 * +
 * `{project_version}` = {project_version}
 *
 * Code block (with syntax highlighting added by CodeRay)::
 * +
 * [source,java]
 * --
 * /**
 *  * = Asciidoclet
 *  *
 *  * A Javadoc Doclet that uses http://asciidoctor.org[Asciidoctor]
 *  * to render http://asciidoc.org[AsciiDoc] markup in Javadoc comments.
 *  *
 *  * @author https://github.com/johncarl81[John Ericksen]
 *  *\/
 * public class Asciidoclet extends Doclet {
 *     private final Asciidoctor asciidoctor = Asciidoctor.Factory.create(); // <1>
 *
 *     @SuppressWarnings("UnusedDeclaration")
 *     public static boolean start(RootDoc rootDoc) {
 *         new Asciidoclet().render(rootDoc); // <2>
 *         return Standard.start(rootDoc);
 *     }
 * }
 * --
 * <1> Creates an instance of the Asciidoctor Java integration
 * <2> Runs Javadoc comment strings through Asciidoctor
 *
 * Inline code:: `code()` or +code()+
 *
 * Headings::
 * +
 * --
 * [float]
 * = Heading 1
 * [float]
 * == Heading 2
 * [float]
 * === Heading 3
 * [float]
 * ==== Heading 4
 * [float]
 * ===== Heading 5
 * --
 *
 * Links::
 * Doc Writer <doc@example.com> +
 * http://asciidoc.org[AsciiDoc] is a lightweight markup language. +
 * Learn more about it at http://asciidoctor.org. +
 *
 * Bullets::
 * +
 * --
 * .Unnumbered
 * * bullet
 * * bullet
 * - bullet
 * - bullet
 * * bullet
 * ** bullet
 * ** bullet
 * *** bullet
 * *** bullet
 * **** bullet
 * **** bullet
 * ***** bullet
 * ***** bullet
 * **** bullet
 * *** bullet
 * ** bullet
 * * bullet
 * --
 * +
 * --
 * .Numbered
 * . bullet
 * . bullet
 * .. bullet
 * .. bullet
 * . bullet
 * .. bullet
 * ... bullet
 * ... bullet
 * .... bullet
 * .... bullet
 * ... bullet
 * ... bullet
 * .. bullet
 * .. bullet
 * . bullet
 * --
 *
 * Tables::
 * +
 * .An example table
 * [cols="3", options="header"]
 * |===
 * |Column 1
 * |Column 2
 * |Column 3
 * 
 * |1
 * |Item 1
 * |a
 * 
 * |2
 * |Item 2
 * |b
 * 
 * |3
 * |Item 3
 * |c
 * |===
 *
 * Sidebar block::
 * +
 * .Optional Title
 * ****
 * *Sidebar* Block
 *
 * Usage: Notes in a sidebar, naturally.
 * ****
 *
 * Admonitions::
 * +
 * IMPORTANT: Check this out!
 *
 * @author https://github.com/johncarl81[John Ericksen]
 * @version {project_version}
 * @see org.asciidoctor.Asciidoclet
 * @since 0.1.0
 * @serial (or @serialField or @serialData)
 */
public class Asciidoclet extends Doclet {

    private final RootDoc rootDoc;
    private final DocletOptions docletOptions;
    private final DocletIterator iterator;
    private final Stylesheets stylesheets;

    public Asciidoclet(RootDoc rootDoc) {
        this.rootDoc = rootDoc;
        this.docletOptions = new DocletOptions(rootDoc);
        this.iterator = new DocletIterator(docletOptions);
        this.stylesheets = new Stylesheets(docletOptions, rootDoc);
    }

    // test use
    Asciidoclet(RootDoc rootDoc, DocletIterator iterator, Stylesheets stylesheets) {
        this.rootDoc = rootDoc;
        this.docletOptions = new DocletOptions(rootDoc);
        this.iterator = iterator;
        this.stylesheets = stylesheets;
    }

    /**
     * .Example usage
     * [source,java]
     * exampleDeprecated("do not use");
     *
     * @deprecated for example purposes
     * @exception Exception example
     * @throws RuntimeException example
     * @serialData something else
     * @link Asciidoclet
     */
    public static void exampleDeprecated(String field) throws Exception{
        //noop
    }

    /**
     * Sets the language version to Java 5.
     *
     * _Javadoc spec requirement._
     *
     * @return language version number
     */
    @SuppressWarnings("UnusedDeclaration")
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }

    /**
     * Sets the option length to the standard Javadoc option length.
     *
     * _Javadoc spec requirement._
     *
     * @param option input option
     * @return length of required parameters
     */
    @SuppressWarnings("UnusedDeclaration")
    public static int optionLength(String option) {
        return optionLength(option, new StandardAdapter());
    }

    /**
     * The starting point of Javadoc render.
     *
     * _Javadoc spec requirement._
     *
     * @param rootDoc input class documents
     * @return success
     */
    @SuppressWarnings("UnusedDeclaration")
    public static boolean start(RootDoc rootDoc) {
        return new Asciidoclet(rootDoc).start(new StandardAdapter());
    }

    /**
     * Processes the input options by delegating to the standard handler.
     *
     * _Javadoc spec requirement._
     *
     * @param options input option array
     * @param errorReporter error handling
     * @return success
     */
    @SuppressWarnings("UnusedDeclaration")
    public static boolean validOptions(String[][] options, DocErrorReporter errorReporter) {
        return validOptions(options, errorReporter, new StandardAdapter());
    }

    static int optionLength(String option, StandardAdapter standardDoclet) {
        return DocletOptions.optionLength(option, standardDoclet);
    }

    static boolean validOptions(String[][] options, DocErrorReporter errorReporter, StandardAdapter standardDoclet) {
        return DocletOptions.validOptions(options, errorReporter, standardDoclet);
    }

    boolean start(StandardAdapter standardDoclet) {
        return run(standardDoclet)
                && postProcess();
    }

    private boolean run(StandardAdapter standardDoclet) {
        AsciidoctorRenderer renderer = new AsciidoctorRenderer(docletOptions, rootDoc);
        try {
            return iterator.render(rootDoc, renderer) &&
                    standardDoclet.start(rootDoc);
        } finally {
            renderer.cleanup();
        }
    }

    private boolean postProcess() {
        if (docletOptions.stylesheetFile().isPresent()) return true;
        return stylesheets.copy();
    }
}
