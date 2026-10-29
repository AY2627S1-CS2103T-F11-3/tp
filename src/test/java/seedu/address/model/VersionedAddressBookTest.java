package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.AddressBookBuilder;

public class VersionedAddressBookTest {

    private final ReadOnlyAddressBook addressBookWithAlice = new AddressBookBuilder().withPerson(ALICE).build();
    private final ReadOnlyAddressBook addressBookWithBenson = new AddressBookBuilder().withPerson(BENSON).build();
    private final ReadOnlyAddressBook addressBookWithCarl = new AddressBookBuilder().withPerson(CARL).build();
    private final ReadOnlyAddressBook emptyAddressBook = new AddressBookBuilder().build();

    @Test
    public void commit_singleAddressBook_noStatesRemoved() {
        VersionedAddressBook versionedAddressBook = new VersionedAddressBook(emptyAddressBook);

        versionedAddressBook.commit();
        assertAddressBookListStatus(versionedAddressBook,
                List.of(emptyAddressBook, emptyAddressBook), 1);
    }

    @Test
    public void commit_multipleAddressBookPointerAtEndOfStateList_noStatesRemoved() {
        VersionedAddressBook versionedAddressBook = prepareAddressBookList(
                emptyAddressBook, addressBookWithAlice, addressBookWithBenson);

        versionedAddressBook.commit();
        assertAddressBookListStatus(versionedAddressBook,
                List.of(emptyAddressBook, addressBookWithAlice, addressBookWithBenson, addressBookWithBenson), 3);
    }

    @Test
    public void commit_multipleAddressBookPointerNotAtEndOfStateList_statesAfterPointerRemoved() {
        VersionedAddressBook versionedAddressBook = prepareAddressBookList(
                emptyAddressBook, addressBookWithAlice, addressBookWithBenson);

        shiftCurrentStatePointerLeftwards(versionedAddressBook, 1);

        versionedAddressBook.commit();
        assertAddressBookListStatus(versionedAddressBook,
                List.of(emptyAddressBook, addressBookWithAlice, addressBookWithAlice), 2);
    }

    @Test
    public void canUndo_multipleAddressBookPointerAtEndOfStateList_returnsTrue() {
        VersionedAddressBook versionedAddressBook = prepareAddressBookList(
                emptyAddressBook, addressBookWithAlice, addressBookWithBenson);

        assertTrue(versionedAddressBook.canUndo());
    }

    @Test
    public void canUndo_multipleAddressBookPointerAtStartOfStateList_returnsFalse() {
        VersionedAddressBook versionedAddressBook = prepareAddressBookList(
                emptyAddressBook, addressBookWithAlice, addressBookWithBenson);

        shiftCurrentStatePointerLeftwards(versionedAddressBook, 2);

        assertFalse(versionedAddressBook.canUndo());
    }

    @Test
    public void canUndo_singleAddressBook_returnsFalse() {
        VersionedAddressBook versionedAddressBook = new VersionedAddressBook(emptyAddressBook);

        assertFalse(versionedAddressBook.canUndo());
    }

    @Test
    public void undo_multipleAddressBookPointerAtEndOfStateList_success() {
        VersionedAddressBook versionedAddressBook = prepareAddressBookList(
                emptyAddressBook, addressBookWithAlice, addressBookWithBenson);

        versionedAddressBook.undo();
        assertAddressBookListStatus(versionedAddressBook,
                List.of(emptyAddressBook, addressBookWithAlice, addressBookWithBenson), 1);
    }

    @Test
    public void undo_multipleAddressBookPointerNotAtStartOfStateList_success() {
        VersionedAddressBook versionedAddressBook = prepareAddressBookList(
                emptyAddressBook, addressBookWithAlice, addressBookWithBenson);

        shiftCurrentStatePointerLeftwards(versionedAddressBook, 1);

        versionedAddressBook.undo();
        assertAddressBookListStatus(versionedAddressBook,
                List.of(emptyAddressBook, addressBookWithAlice, addressBookWithBenson), 0);
    }

    @Test
    public void undo_singleAddressBook_throwsNoUndoableStateException() {
        VersionedAddressBook versionedAddressBook = new VersionedAddressBook(emptyAddressBook);

        assertThrows(VersionedAddressBook.NoUndoableStateException.class, versionedAddressBook::undo);
    }

    @Test
    public void undo_multipleAddressBookPointerAtStartOfStateList_throwsNoUndoableStateException() {
        VersionedAddressBook versionedAddressBook = prepareAddressBookList(
                emptyAddressBook, addressBookWithAlice, addressBookWithBenson);

        shiftCurrentStatePointerLeftwards(versionedAddressBook, 2);

        assertThrows(VersionedAddressBook.NoUndoableStateException.class, versionedAddressBook::undo);
    }

    @Test
    public void equals() {
        VersionedAddressBook versionedAddressBook = prepareAddressBookList(addressBookWithAlice, addressBookWithBenson);

        // same values -> returns true
        VersionedAddressBook copy = prepareAddressBookList(addressBookWithAlice, addressBookWithBenson);
        assertEquals(versionedAddressBook, copy);

        // same object -> returns true
        assertEquals(versionedAddressBook, versionedAddressBook);

        // null -> returns false
        assertNotEquals(null, versionedAddressBook);

        // different types -> returns false
        assertNotEquals(1, versionedAddressBook);

        // different state list -> returns false
        VersionedAddressBook differentAddressBookList =
                prepareAddressBookList(addressBookWithAlice, addressBookWithCarl);
        assertNotEquals(versionedAddressBook, differentAddressBookList);

        // different current pointer -> returns false
        VersionedAddressBook differentCurrentStatePointer = prepareAddressBookList(
                addressBookWithAlice, addressBookWithBenson);
        shiftCurrentStatePointerLeftwards(differentCurrentStatePointer, 1);
        assertNotEquals(versionedAddressBook, differentCurrentStatePointer);
    }

    private VersionedAddressBook prepareAddressBookList(ReadOnlyAddressBook... addressBooks) {
        assertTrue(addressBooks.length > 0);

        VersionedAddressBook versionedAddressBook = new VersionedAddressBook(addressBooks[0]);
        for (int i = 1; i < addressBooks.length; i++) {
            versionedAddressBook.resetData(addressBooks[i]);
            versionedAddressBook.commit();
        }

        return versionedAddressBook;
    }

    private void shiftCurrentStatePointerLeftwards(VersionedAddressBook versionedAddressBook, int count) {
        for (int i = 0; i < count; i++) {
            versionedAddressBook.undo();
        }
    }

    private void assertAddressBookListStatus(VersionedAddressBook versionedAddressBook,
                                             List<ReadOnlyAddressBook> expectedAddressBookStateList,
                                             int expectedCurrentStatePointer) {
        assertEquals(expectedAddressBookStateList.get(expectedCurrentStatePointer),
                new AddressBook(versionedAddressBook));
    }
}
