package ru.kiseru.tablereportcreator

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream


class MainTest {

    @Test
    fun `test setting title`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when
        createReport(outputStream) {
            setTitle("Some cool title")
        }

        // then
        assertThat(outputStream.toString()).isEqualTo(" Some cool title\n")
    }

    @Test
    fun `test setting title twice`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when & then
        assertThatExceptionOfType(IllegalStateException::class.java)
            .isThrownBy {
                createReport(outputStream) {
                    setTitle("Some cool title")
                    setTitle("Another cool title")
                }
            }
            .withMessage("Report title is already set")
    }

    @Test
    fun `test setting title after creating table`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when & then
        assertThatExceptionOfType(IllegalStateException::class.java)
            .isThrownBy {
                createReport(outputStream) {
                    createTable(0) {}
                    setTitle("Some cool title")
                }
            }
            .withMessage("Cannot set report title after creating tables")
    }

    @Test
    fun `test setting table title without report title`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when
        createReport(outputStream) {
            createTable(0, 0) {
                setTitle("Some cool table title")
            }
        }

        // then
        assertThat(outputStream.toString()).isEqualTo(" Some cool table title\n")
    }

    @Test
    fun `test setting table title with report title`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when
        createReport(outputStream) {
            setTitle("Some cool report title")

            createTable(0, 0) {
                setTitle("Some cool table title")
            }
        }

        // then
        assertThat(outputStream.toString()).isEqualTo(" Some cool report title\n\n Some cool table title\n")
    }

    @Test
    fun `test setting table title twice`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when & then
        assertThatExceptionOfType(IllegalStateException::class.java)
            .isThrownBy {
                createReport(outputStream) {
                    createTable(0, 0) {
                        setTitle("Some cool table title")
                        setTitle("Another cool table title")
                    }
                }
            }
            .withMessage("Table title is already set")
    }

    @Test
    fun `test setting table title after setting table header`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when & then
        assertThatExceptionOfType(IllegalStateException::class.java)
            .isThrownBy {
                createReport(outputStream) {
                    createTable(3, 3) {
                        setHeaders("A", "B")
                        setTitle("Some cool table title")
                    }
                }
            }
            .withMessage("Table headers is already set")
    }

    @Test
    fun `test setting table title after setting data`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when & then
        assertThatExceptionOfType(IllegalStateException::class.java)
            .isThrownBy {
                createReport(outputStream) {
                    createTable(3, 3) {
                        data(
                            listOf("A", "B"),
                        )
                        setTitle("Some cool table title")
                    }
                }
            }
            .withMessage("Table data is already set")
    }

    @Test
    fun `test setting table headers`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when
        createReport(outputStream) {
            createTable(3, 3) {
                setHeaders("A", "B")
            }
        }

        // then
        assertThat(outputStream.toString())
            .isEqualTo("""
                ┌───┬───┐
                │ A │ B │
                └───┴───┘${'\n'}
            """.trimIndent())
    }

    @Test
    fun `test setting table headers twice`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when & then
        assertThatExceptionOfType(IllegalStateException::class.java)
            .isThrownBy {
                createReport(outputStream) {
                    createTable(3, 3) {
                        setHeaders("A", "B")
                        setHeaders("A", "B")
                    }
                }
            }
            .withMessage("Table headers is already set")
    }

    @Test
    fun `test setting table headers after setting data`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when & then
        assertThatExceptionOfType(IllegalStateException::class.java)
            .isThrownBy {
                createReport(outputStream) {
                    createTable(3, 3) {
                        data(
                            listOf("1", "2")
                        )
                        setHeaders("A", "B")
                    }
                }
            }
            .withMessage("Table data is already set")
    }

    @Test
    fun `test setting table data twice`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when & then
        assertThatExceptionOfType(IllegalStateException::class.java)
            .isThrownBy {
                createReport(outputStream) {
                    createTable(3, 3) {
                        data(
                            listOf("1", "2")
                        )
                        data(
                            listOf("3", "4")
                        )
                    }
                }
            }
            .withMessage("Table data is already set")
    }

    @Test
    fun `test setting table data without table headers`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when
        createReport(outputStream) {
            createTable(3, 3) {
                data(listOf("1", "2"))
            }
        }

        // then
        assertThat(outputStream.toString())
            .isEqualTo("""
                ┌───┬───┐
                │ 1 │ 2 │
                └───┴───┘${'\n'}
            """.trimIndent())
    }

    @Test
    fun `test setting table data with table headers`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when
        createReport(outputStream) {
            createTable(3, 3) {
                setHeaders("A", "B")
                data(listOf("1", "2"))
            }
        }

        // then
        assertThat(outputStream.toString())
            .isEqualTo("""
                ┌───┬───┐
                │ A │ B │
                ├───┼───┤
                │ 1 │ 2 │
                └───┴───┘${'\n'}
            """.trimIndent())
    }

    @Test
    fun `test setting table data more than columns exists`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when & then
        assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy {
                createReport(outputStream) {
                    createTable(3, 3) {
                        data(
                            listOf("1", "2", "3")
                        )
                    }
                }
            }
            .withMessage("The table have 2 columns.")
    }

    @Test
    fun `test setting table data with value more than column size`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when
        createReport(outputStream) {
            createTable(3, 3) {
                data(listOf("12", "2"))
            }
        }

        // then
        assertThat(outputStream.toString())
            .isEqualTo("""
                ┌───┬───┐
                │ 1 │ 2 │
                └───┴───┘${'\n'}
            """.trimIndent())
    }

    @Test
    fun `test setting table data with value less than column size`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when
        createReport(outputStream) {
            createTable(4, 3) {
                data(listOf("1", "2"))
            }
        }

        // then
        assertThat(outputStream.toString())
            .isEqualTo("""
                ┌────┬───┐
                │ 1  │ 2 │
                └────┴───┘${'\n'}
            """.trimIndent())
    }

    @Test
    fun `test setting table headers more than columns exists`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when & then
        assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy {
                createReport(outputStream) {
                    createTable(3, 3) {
                        setHeaders("A", "B", "C")
                    }
                }
            }
            .withMessage("The table have 2 columns.")
    }

    @Test
    fun `test setting table headers #1`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when
        createReport(outputStream) {
            createTable(5, 3) {
                setHeaders("A", "B")
            }
        }

        // then
        assertThat(outputStream.toString())
            .isEqualTo("""
                ┌─────┬───┐
                │  A  │ B │
                └─────┴───┘${'\n'}
            """.trimIndent())
    }

    @Test
    fun `test setting table headers #2`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when
        createReport(outputStream) {
            createTable(4, 3) {
                setHeaders("A", "B")
            }
        }

        // then
        assertThat(outputStream.toString())
            .isEqualTo("""
                ┌────┬───┐
                │ A  │ B │
                └────┴───┘${'\n'}
            """.trimIndent())
    }

    @Test
    fun `test setting table headers #3`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when
        createReport(outputStream) {
            createTable(5, 3) {
                setHeaders("AC", "B")
            }
        }

        // then
        assertThat(outputStream.toString())
            .isEqualTo("""
                ┌─────┬───┐
                │ AC  │ B │
                └─────┴───┘${'\n'}
            """.trimIndent())
    }

    @Test
    fun `test setting table headers #4`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when
        createReport(outputStream) {
            createTable(6, 3) {
                setHeaders("AC", "B")
            }
        }

        // then
        assertThat(outputStream.toString())
            .isEqualTo("""
                ┌──────┬───┐
                │  AC  │ B │
                └──────┴───┘${'\n'}
            """.trimIndent())
    }

    @Test
    fun `test setting table headers #5`() {
        // given
        val outputStream = ByteArrayOutputStream()

        // when
        createReport(outputStream) {
            createTable(3, 3) {
                setHeaders("AC", "B")
            }
        }

        // then
        assertThat(outputStream.toString())
            .isEqualTo("""
                ┌───┬───┐
                │ A │ B │
                └───┴───┘${'\n'}
            """.trimIndent())
    }
}