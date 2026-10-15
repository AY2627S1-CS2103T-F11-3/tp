package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SampleDataUtilTest {

    @Test
    public void getSampleAddressBook_containsAllSamplePersons() {
        assertEquals(SampleDataUtil.getSamplePersons().length,
                SampleDataUtil.getSampleAddressBook().getPersonList().size());
    }
}
