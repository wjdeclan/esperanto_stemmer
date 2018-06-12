## Esperanto Stemmer
#### Declan Whitford Jones

A stemmer for Esperanto

This stemmer takes these steps to produce a stem:
  1. Lowercase the word
  2. Stop stemming if the word is one of a variety of exceptions (conjunctions, interjections, prepositions, etc.)
  3. Stop stemming if the word is a number 1-10
  4. Stop stemming if the word is a larger number (i.e. 11 can be written dek unu [ten one] or dekunu, this finds the second)
  5. Determine if the word has the plural or direct object suffixes
  6. Determine the longest other suffix that does not produce a stem of a smaller size than minStemLength
  7. If no such suffix exists, return the word minus the plural and direct object suffixes
  8. Otherwise, return the word minus the found suffix

---

   Copyright 2018 Declan Whitford Jones

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

---