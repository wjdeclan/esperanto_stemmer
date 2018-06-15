## Esperanto Stemmer
#### Declan Whitford Jones

A stemmer for Esperanto

This stemmer takes these steps to produce a stem:
  1. Lowercase the word
  2. Stop stemming if the word is one of a variety of exceptions (conjunctions, interjections, prepositions, etc.)
  3. Stop stemming if the word is a number 1-10
  4. Stop stemming if the word is a larger number (i.e. 11 can be written dek unu [ten one] or dekunu, this finds the second)
  5. Determine if the word has the plural or direct object suffixes
  6. Determine the longest other suffix that does not produce a stem of a smaller size than minStemLength or the position of the first vowel, whichever is longer
  7. If no such suffix exists, return the word
  8. Otherwise, return the word minus the found suffix

---

   Copyright (C) 2018 Declan Whitford Jones

   Licensed under GPL v3

---