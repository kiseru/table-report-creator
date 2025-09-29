package ru.kiseru.tablereportcreator

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test


class MainTest {

    @Test
    fun `test setting title`() {
        // when
        val report = createReport {
            setTitle("Some cool title")
        }

        // then
        assertThat(report).isEqualTo(" Some cool title\n")
    }

    @Test
    fun `test setting title twice`() {
        // when & then
        assertThatExceptionOfType(IllegalStateException::class.java)
            .isThrownBy {
                createReport {
                    setTitle("Some cool title")
                    setTitle("Another cool title")
                }
            }
            .withMessage("Report title is already set")
    }

    @Test
    fun `test setting title after creating table`() {
        // when & then
        assertThatExceptionOfType(IllegalStateException::class.java)
            .isThrownBy {
                createReport {
                    createTable(0) {}
                    setTitle("Some cool title")
                }
            }
            .withMessage("Cannot set report title after creating tables")
    }

    @Test
    fun `test setting table title without report title`() {
        // when
        val result = createReport {
            createTable(0, 0) {
                setTitle("Some cool table title")
            }
        }

        // then
        assertThat(result).isEqualTo(" Some cool table title\n")
    }

    @Test
    fun `test setting table title with report title`() {
        // when
        val result = createReport {
            setTitle("Some cool report title")

            createTable(0, 0) {
                setTitle("Some cool table title")
            }
        }

        // then
        assertThat(result).isEqualTo(" Some cool report title\n\n Some cool table title\n")
    }

    @Test
    fun `test setting table title twice`() {
        // when & then
        assertThatExceptionOfType(IllegalStateException::class.java)
            .isThrownBy {
                createReport {
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
        // when & then
        assertThatExceptionOfType(IllegalStateException::class.java)
            .isThrownBy {
                createReport {
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
        // when & then
        assertThatExceptionOfType(IllegalStateException::class.java)
            .isThrownBy {
                createReport {
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
        // when
        val report = createReport {
            createTable(3, 3) {
                setHeaders("A", "B")
            }
        }

        // then
        assertThat(report)
            .isEqualTo("""
                ┌───┬───┐
                │ A │ B │
                └───┴───┘${'\n'}
            """.trimIndent())
    }

    @Test
    fun `test setting table headers twice`() {
        // when & then
        assertThatExceptionOfType(IllegalStateException::class.java)
            .isThrownBy {
                createReport {
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
        // when & then
        assertThatExceptionOfType(IllegalStateException::class.java)
            .isThrownBy {
                createReport {
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
        // when & then
        assertThatExceptionOfType(IllegalStateException::class.java)
            .isThrownBy {
                createReport {
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
        // when
        val report = createReport {
            createTable(3, 3) {
                data(listOf("1", "2"))
            }
        }

        // then
        assertThat(report)
            .isEqualTo("""
                ┌───┬───┐
                │ 1 │ 2 │
                └───┴───┘${'\n'}
            """.trimIndent())
    }

    @Test
    fun `test setting table data with table headers`() {
        // when
        val report = createReport {
            createTable(3, 3) {
                setHeaders("A", "B")
                data(listOf("1", "2"))
            }
        }

        // then
        assertThat(report)
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
        // when & then
        assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy {
                createReport {
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
        // when
        val report = createReport {
            createTable(3, 3) {
                data(listOf("12", "2"))
            }
        }

        // then
        assertThat(report)
            .isEqualTo("""
                ┌───┬───┐
                │ 1 │ 2 │
                └───┴───┘${'\n'}
            """.trimIndent())
    }

    @Test
    fun `test setting table data with value less than column size`() {
        // when
        val report = createReport {
            createTable(4, 3) {
                data(listOf("1", "2"))
            }
        }

        // then
        assertThat(report)
            .isEqualTo("""
                ┌────┬───┐
                │ 1  │ 2 │
                └────┴───┘${'\n'}
            """.trimIndent())
    }

    @Test
    fun `test setting table headers more than columns exists`() {
        // when & then
        assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy {
                createReport {
                    createTable(3, 3) {
                        setHeaders("A", "B", "C")
                    }
                }
            }
            .withMessage("The table have 2 columns.")
    }

    @Test
    fun `test setting table headers #1`() {
        // when
        val report = createReport {
            createTable(5, 3) {
                setHeaders("A", "B")
            }
        }

        // then
        assertThat(report)
            .isEqualTo("""
                ┌─────┬───┐
                │  A  │ B │
                └─────┴───┘${'\n'}
            """.trimIndent())
    }

    @Test
    fun `test setting table headers #2`() {
        // when
        val report = createReport {
            createTable(4, 3) {
                setHeaders("A", "B")
            }
        }

        // then
        assertThat(report)
            .isEqualTo("""
                ┌────┬───┐
                │ A  │ B │
                └────┴───┘${'\n'}
            """.trimIndent())
    }

    @Test
    fun `test setting table headers #3`() {
        // when
        val report = createReport {
            createTable(5, 3) {
                setHeaders("AC", "B")
            }
        }

        // then
        assertThat(report)
            .isEqualTo("""
                ┌─────┬───┐
                │ AC  │ B │
                └─────┴───┘${'\n'}
            """.trimIndent())
    }

    @Test
    fun `test setting table headers #4`() {
        // when
        val report = createReport {
            createTable(6, 3) {
                setHeaders("AC", "B")
            }
        }

        // then
        assertThat(report)
            .isEqualTo("""
                ┌──────┬───┐
                │  AC  │ B │
                └──────┴───┘${'\n'}
            """.trimIndent())
    }

    @Test
    fun `test setting table headers #5`() {
        // when
        val report = createReport {
            createTable(3, 3) {
                setHeaders("AC", "B")
            }
        }

        // then
        assertThat(report)
            .isEqualTo("""
                ┌───┬───┐
                │ A │ B │
                └───┴───┘${'\n'}
            """.trimIndent())
    }
}