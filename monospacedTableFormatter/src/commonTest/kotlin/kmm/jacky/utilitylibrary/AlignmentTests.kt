package kmm.jacky.utilitylibrary

import kmm.jacky.utilitylibrary.enums.Alignment
import kmm.jacky.utilitylibrary.enums.Alignment.Bottom
import kmm.jacky.utilitylibrary.enums.Alignment.Center
import kmm.jacky.utilitylibrary.enums.Alignment.CenterHorizontally
import kmm.jacky.utilitylibrary.enums.Alignment.CenterVertically
import kmm.jacky.utilitylibrary.enums.Alignment.End
import kmm.jacky.utilitylibrary.enums.Alignment.Start
import kmm.jacky.utilitylibrary.enums.Alignment.Top
import kmm.jacky.utilitylibrary.enums.Alignment.Undefined
import kmm.jacky.utilitylibrary.enums.JointedAlignments
import kmm.jacky.utilitylibrary.extensions.buildPrefix
import kmm.jacky.utilitylibrary.extensions.canJoinWith
import kmm.jacky.utilitylibrary.extensions.getRowIndexAfterOffset
import kmm.jacky.utilitylibrary.extensions.horizontal
import kmm.jacky.utilitylibrary.extensions.isCenter
import kmm.jacky.utilitylibrary.extensions.isHorizontal
import kmm.jacky.utilitylibrary.extensions.isVertical
import kmm.jacky.utilitylibrary.extensions.plus
import kmm.jacky.utilitylibrary.extensions.update
import kmm.jacky.utilitylibrary.extensions.vertical
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AlignmentTests {

    @Test
    fun testHorizontalAlignments() {
        assertTrue(Start.isHorizontal())
        assertEquals(Start, Start.horizontal())

        assertTrue(End.isHorizontal())
        assertEquals(End, End.horizontal())

        assertTrue(CenterHorizontally.isHorizontal())
        assertEquals(CenterHorizontally, CenterHorizontally.horizontal())

        assertTrue(Center.isHorizontal())
        assertEquals(CenterHorizontally, Center.horizontal())

        assertFalse(Top.isHorizontal())
        assertEquals(Start, Top.horizontal())

        assertFalse(Bottom.isHorizontal())
        assertEquals(Start, Bottom.horizontal())

        assertFalse(CenterVertically.isHorizontal())
        assertEquals(Start, CenterVertically.horizontal())

        assertFalse(Undefined.isHorizontal())
        assertEquals(Start, Undefined.horizontal())
    }

    @Test
    fun testVerticalAlignments() {
        assertTrue(Top.isVertical())
        assertEquals(Top, Top.vertical())

        assertTrue(Bottom.isVertical())
        assertEquals(Bottom, Bottom.vertical())

        assertTrue(CenterVertically.isVertical())
        assertEquals(CenterVertically, CenterVertically.vertical())

        assertTrue(Center.isVertical())
        assertEquals(CenterVertically, Center.vertical())

        assertFalse(Start.isVertical())
        assertEquals(Top, Start.vertical())

        assertFalse(End.isVertical())
        assertEquals(Top, End.vertical())

        assertFalse(CenterHorizontally.isVertical())
        assertEquals(Top, CenterHorizontally.vertical())

        assertFalse(Undefined.isVertical())
        assertEquals(Top, Undefined.vertical())
    }

    @Test
    fun testJointAlignments() {
        fun assertCanJoin(alignment: Alignment, vararg other: Alignment) = other.forEach {
            assertTrue(alignment.canJoinWith(it))
            assertTrue(it.canJoinWith(alignment))
            ((alignment + it) as JointedAlignments).alignments.run {
                assertTrue(contains(alignment))
                assertTrue(contains(it))
            }
            ((it + alignment) as JointedAlignments).alignments.run {
                assertTrue(contains(alignment))
                assertTrue(contains(it))
            }
        }

        assertCanJoin(Start, Top, Bottom, CenterVertically)
        assertCannotJoin(Start, Start, End, CenterHorizontally, Undefined)

        assertCanJoin(End, Top, Bottom, CenterVertically)
        assertCannotJoin(End, Start, End, CenterHorizontally, Undefined)

        assertCanJoin(CenterHorizontally, Top, Bottom, CenterVertically)
        assertCannotJoin(CenterHorizontally, Start, End, CenterHorizontally, Undefined)

        assertCanJoin(Top, Start, End, CenterHorizontally)
        assertCannotJoin(Top, Top, Bottom, CenterVertically, Undefined)

        assertCanJoin(Bottom, Start, End, CenterHorizontally)
        assertCannotJoin(Bottom, Top, Bottom, CenterVertically, Undefined)

        assertCanJoin(CenterVertically, Start, End, CenterHorizontally)
        assertCannotJoin(CenterVertically, Top, Bottom, CenterVertically, Undefined)
    }

    @Test
    fun testJointedAlignmentWithoutShifted() {
        fun assertJointHorizontal(horizontal: Alignment, vertical: Alignment) {
            assertEquals(horizontal, (horizontal + vertical).horizontal())
            assertEquals(vertical, (horizontal + vertical).vertical())
        }

        val horizontals = listOf(Start, CenterHorizontally, End)
        val verticals = listOf(Top, CenterVertically, Bottom)

        horizontals.forEach { horizontal ->
            verticals.forEach { vertical ->
                assertJointHorizontal(horizontal, vertical)
            }
        }
    }

    @Test
    fun testJointedAlignmentForCenter() {
        fun assert(alignment: Alignment, horizontal: Alignment, vertical: Alignment) {
            ((Center + alignment) as JointedAlignments).also {
                assertEquals(horizontal, it.horizontal())
                assertEquals(vertical, it.vertical())
            }
            ((alignment + Center) as JointedAlignments).also {
                assertEquals(horizontal, it.horizontal())
                assertEquals(vertical, it.vertical())
            }
        }

        assert(Start, Start, CenterVertically)
        assert(CenterHorizontally, CenterHorizontally, CenterVertically)
        assert(End, End, CenterVertically)
        assert(Top, CenterHorizontally, Top)
        assert(CenterVertically, CenterHorizontally, CenterVertically)
        assert(Bottom, CenterHorizontally, Bottom)
    }

    @Test
    fun testCenterAlignments() {
        fun assertCenterJoint(other: Alignment, transformed: Alignment) {
            assertTrue(Center.canJoinWith(other))
            ((Center + other) as JointedAlignments).alignments.run {
                assertTrue(contains(other))
                assertTrue(contains(transformed))
            }
            ((other + Center) as JointedAlignments).alignments.run {
                assertTrue(contains(other))
                assertTrue(contains(transformed))
            }
        }

        assertTrue(Center.isCenter())
        assertTrue(CenterVertically.isCenter())
        assertTrue(CenterHorizontally.isCenter())

        assertFalse(Center.canJoinWith(Center))

        assertCenterJoint(Start, CenterVertically)
        assertCenterJoint(CenterHorizontally, CenterVertically)
        assertCenterJoint(End, CenterVertically)

        assertCenterJoint(Top, CenterHorizontally)
        assertCenterJoint(CenterVertically, CenterHorizontally)
        assertCenterJoint(Bottom, CenterHorizontally)
    }

    @Test
    fun testUndefinedAlignments() {
        assertFalse(Undefined.isHorizontal())
        assertFalse(Undefined.isVertical())
        assertFalse(Undefined.isCenter())

        assertEquals(Start, Undefined.horizontal())
        assertEquals(Top, Undefined.vertical())

        assertCannotJoin(
            Undefined,
            Start,
            End,
            CenterHorizontally,
            Center,
            Top,
            Bottom,
            CenterVertically,
            Undefined
        )
    }

    @Test
    fun testAlignmentUpdate() {
        fun assertUpdate(alignment: Alignment, transformed: Alignment, expect: Alignment) {
            assertEquals(expect, alignment.update(transformed))
        }

        val options = listOf(
            Start, CenterHorizontally, End,
            Top, CenterVertically, Bottom,
            Center
        )
        options.forEach { lhs ->
            options.forEach { rhs ->
                assertUpdate(lhs, rhs, lhs)
                assertUpdate(rhs, lhs, rhs)
            }
            assertUpdate(lhs, Undefined, lhs)
            assertUpdate(Undefined, lhs, lhs)
        }
        assertUpdate(Undefined, Undefined, Undefined)
    }

    private fun assertCannotJoin(alignment: Alignment, vararg other: Alignment) = other.forEach {
        assertFalse(alignment.canJoinWith(it))
        assertFalse(it.canJoinWith(alignment))
    }

    @Test
    fun testHorizontalStringAlignmentBuilderEvenBoundary() {
        val input = "Hello world".length
        val boundary = 20
        assertEquals(0, Start.buildPrefix(input, boundary))              // "Hello world         "
        assertEquals(5, CenterHorizontally.buildPrefix(input, boundary)) // "     Hello world    "
        assertEquals(9, End.buildPrefix(input, boundary))                // "         Hello world"
        assertEquals(5, Center.buildPrefix(input, boundary))             // "     Hello world    "
        assertEquals(0, Top.buildPrefix(input, boundary))                // "Hello world         "
        assertEquals(0, CenterVertically.buildPrefix(input, boundary))   // "Hello world         "
        assertEquals(0, Bottom.buildPrefix(input, boundary))             // "Hello world         "
        assertEquals(0, Undefined.buildPrefix(input, boundary))          // "Hello world         "
    }

    @Test
    fun testHorizontalStringAlignmentBuilderOddBoundary() {
        val input = "Hello world".length
        val boundary = 21
        assertEquals(0, Start.buildPrefix(input, boundary))              // "Hello world          "
        assertEquals(5, CenterHorizontally.buildPrefix(input, boundary)) // "     Hello world     "
        assertEquals(10, End.buildPrefix(input, boundary))               // "          Hello world"
        assertEquals(5, Center.buildPrefix(input, boundary))             // "     Hello world     "
        assertEquals(0, Top.buildPrefix(input, boundary))                // "Hello world          "
        assertEquals(0, CenterVertically.buildPrefix(input, boundary))   // "Hello world          "
        assertEquals(0, Bottom.buildPrefix(input, boundary))             // "Hello world          "
        assertEquals(0, Undefined.buildPrefix(input, boundary))          // "Hello world          "
    }

    @Test
    fun testGetRowIndexAfterAlignmentOffset() {
        // odd input size, even max row
        assertEquals(0, Top.getRowIndexAfterOffset(1, 0, 2))
        assertEquals(-1, Top.getRowIndexAfterOffset(1, 1, 2))

        assertEquals(0, CenterVertically.getRowIndexAfterOffset(1, 0, 2))
        assertEquals(-1, CenterVertically.getRowIndexAfterOffset(1, 1, 2))

        assertEquals(-1, Bottom.getRowIndexAfterOffset(1, 0, 2))
        assertEquals(0, Bottom.getRowIndexAfterOffset(1, 1, 2))

        // odd input size, odd max row
        assertEquals(0, Top.getRowIndexAfterOffset(1, 0, 3))
        assertEquals(-1, Top.getRowIndexAfterOffset(1, 1, 3))
        assertEquals(-1, Top.getRowIndexAfterOffset(1, 2, 3))

        assertEquals(-1, CenterVertically.getRowIndexAfterOffset(1, 0, 3))
        assertEquals(0, CenterVertically.getRowIndexAfterOffset(1, 1, 3))
        assertEquals(-1, CenterVertically.getRowIndexAfterOffset(1, 2, 3))

        assertEquals(-1, Bottom.getRowIndexAfterOffset(1, 0, 3))
        assertEquals(-1, Bottom.getRowIndexAfterOffset(1, 1, 3))
        assertEquals(0, Bottom.getRowIndexAfterOffset(1, 2, 3))

        // even input size, even max row
        assertEquals(0, Top.getRowIndexAfterOffset(2, 0, 2))
        assertEquals(1, Top.getRowIndexAfterOffset(2, 1, 2))

        assertEquals(0, CenterVertically.getRowIndexAfterOffset(2, 0, 2))
        assertEquals(1, CenterVertically.getRowIndexAfterOffset(2, 1, 2))

        assertEquals(0, Bottom.getRowIndexAfterOffset(2, 0, 2))
        assertEquals(1, Bottom.getRowIndexAfterOffset(2, 1, 2))

        // even input size, odd max row
        assertEquals(0, Top.getRowIndexAfterOffset(2, 0, 3))
        assertEquals(1, Top.getRowIndexAfterOffset(2, 1, 3))
        assertEquals(-1, Top.getRowIndexAfterOffset(2, 2, 3))

        assertEquals(0, CenterVertically.getRowIndexAfterOffset(2, 0, 3))
        assertEquals(1, CenterVertically.getRowIndexAfterOffset(2, 1, 3))
        assertEquals(-1, CenterVertically.getRowIndexAfterOffset(2, 2, 3))

        assertEquals(-1, Bottom.getRowIndexAfterOffset(2, 0, 3))
        assertEquals(0, Bottom.getRowIndexAfterOffset(2, 1, 3))
        assertEquals(1, Bottom.getRowIndexAfterOffset(2, 2, 3))
    }
/*
    @Test
    fun testVerticalStringAlignmentBuilder() {
        fun assert(
            jointed: Alignment,
            input: List<String>,
            maxRow: Int,
            boundary: Int,
            vararg expect: String
        ) {
            assertEquals(
                expect.toList(),
                jointed.buildContent(input, boundary, maxRow)
            )
        }

        assert(
            Start + Top, listOf("Hello world"), 2, 16,
            "Hello world     ", "                "
        )

        assert(
            Start + CenterVertically, listOf("Hello world"), 2, 16,
            "Hello world     ", "                "
        )

        assert(
            Start + Bottom, listOf("Hello world"), 2, 16,
            "                ", "Hello world     "
        )

        assert(
            Start + Top, listOf("Hello world"), 3, 16,
            "Hello world     ", "                ", "                "
        )

        assert(
            Start + CenterVertically, listOf("Hello world"), 3, 16,
            "                ", "Hello world     ", "                "
        )

        assert(
            Start + Bottom, listOf("Hello world"), 3, 16,
            "                ", "                ", "Hello world     "
        )

        assert(
            Start + Top, listOf("Hello", "world"), 2, 5,
            "Hello", "world"
        )

        assert(
            Start + CenterVertically, listOf("Hello", "world"), 2, 5,
            "Hello", "world"
        )

        assert(
            Start + Bottom, listOf("Hello", "world"), 2, 5,
            "Hello", "world"
        )

        assert(
            Start + Top, listOf("Hello", "world"), 3, 5,
            "Hello", "world", "     "
        )

        assert(
            Start + CenterVertically, listOf("Hello", "world"), 3, 5,
            "Hello", "world", "     "
        )

        assert(
            Start + Bottom, listOf("Hello", "world"), 3, 5,
            "     ", "Hello", "world"
        )

        assert(
            Start + Top, listOf("Hello", "world"), 4, 5,
            "Hello", "world", "     ", "     "
        )
        assert(
            Start + CenterVertically, listOf("Hello", "world"), 4, 5,
            "     ", "Hello", "world", "     "
        )
        assert(
            Start + Bottom, listOf("Hello", "world"), 4, 5,
            "     ", "     ", "Hello", "world",
        )
    }*/
}
