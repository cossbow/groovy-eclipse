/*
 * Copyright 2009-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.eclipse.refactoring.test.extract

import static org.codehaus.groovy.eclipse.refactoring.test.extract.ExtractLocalTestsData.*

import org.codehaus.groovy.eclipse.refactoring.core.extract.ExtractGroovyLocalRefactoring
import org.codehaus.groovy.eclipse.refactoring.test.RefactoringTestSuite
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.ltk.core.refactoring.RefactoringCore
import org.eclipse.ltk.core.refactoring.RefactoringStatus
import org.junit.Test

final class ExtractLocalTests extends RefactoringTestSuite {

    @Override
    protected String getRefactoringPath() {
        null
    }

    private void helper(String before, String expected, int offset, int length, boolean replaceAllOccurrences) {
        GroovyCompilationUnit cu = (GroovyCompilationUnit) createCU(packageP, 'A.groovy', before)
        ExtractGroovyLocalRefactoring refactoring = new ExtractGroovyLocalRefactoring(cu, offset, length)
        refactoring.setReplaceAllOccurrences(replaceAllOccurrences)
        refactoring.setLocalName(refactoring.guessLocalNames()[0])
        RefactoringStatus result = performRefactoring(refactoring, false)
        assert result == null || result.isOK() : 'was supposed to pass'
        assertEqualLines('invalid extraction', expected, cu.getSource())

        assert RefactoringCore.getUndoManager().anythingToUndo() : 'anythingToUndo'
        assert !RefactoringCore.getUndoManager().anythingToRedo() : '! anythingToRedo'

        RefactoringCore.getUndoManager().performUndo(null, new NullProgressMonitor())
        assertEqualLines('invalid undo', before, cu.getSource())

        assert !RefactoringCore.getUndoManager().anythingToUndo() : '! anythingToUndo'
        assert RefactoringCore.getUndoManager().anythingToRedo() : 'anythingToRedo'

        RefactoringCore.getUndoManager().performRedo(null, new NullProgressMonitor())
        assertEqualLines('invalid redo', expected, cu.getSource())
    }

    @Test
    void test1() {
        helper(getTest1In(), getTest1Out(), findLocation('foo + bar', 'test1'), 'foo + bar'.length(), true)
    }

    @Test
    void test2() {
        helper(getTest2In(), getTest2Out(), findLocation('foo.bar', 'test2'), 'foo.bar'.length(), true)
    }

    @Test
    void test3() {
        helper(getTest3In(), getTest3Out(), findLocation('baz.foo.&bar', 'test3'), 'baz.foo.&bar'.length(), true)
    }

    @Test
    void test4() {
        helper(getTest4In(), getTest4Out(), findLocation('first + 1', 'test4'), 'first + 1'.length(), false)
    }

    @Test
    void test5() {
        helper(getTest5In(), getTest5Out(), findLocation('foo + bar', 'test5'), 'foo + bar'.length(), true)
    }

    @Test
    void test6() {
        helper(getTest6In(), getTest6Out(), findLocation('foo + bar', 'test6'), 'foo + bar'.length(), true)
    }

    @Test
    void test7() {
        helper(getTest7In(), getTest7Out(), findLocation('foo + bar', 'test7'), 'foo + bar'.length(), true)
    }

    @Test
    void test8() {
        helper(getTest8In(), getTest8Out(), findLocation('foo+  bar', 'test8'), 'foo+  bar'.length(), true)
    }

    @Test
    void test9() {
        helper(getTest9In(), getTest9Out(), findLocation('map.one', 'test9'), 'map.one'.length(), true)
    }

    @Test
    void test10() {
        helper(getTest10In(), getTest10Out(), findLocation('model.farInstance()', 'test10'), 'model.farInstance()'.length(), true)
    }

    @Test
    void test10a() {
        helper(getTest10In(), getTest10Out(), findLocation('model.farInstance() ', 'test10'), 'model.farInstance() '.length(), true)
    }

    @Test
    void test10b() {
        helper(getTest10In(), getTest10Out(), findLocation('model.farInstance()  ', 'test10'), 'model.farInstance()  '.length(), true)
    }

    @Test
    void test11() {
        helper(getTest11In(), getTest11Out(), findLocation('println "here"', 'test11'), 'println "here"'.length(), true)
    }

    @Test
    void test12() {
        helper(getTest12In(), getTest12Out(), findLocation('println "here"', 'test12'), 'println "here"'.length(), true)
    }

    @Test
    void test13() {
        helper(getTest13In(), getTest13Out(), findLocation('a + b', 'test13'), 'a + b'.length(), true)
    }
}
