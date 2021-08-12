import React from 'react'
import { Container, Table } from 'semantic-ui-react'

export const TrainingWords = () => {
	const getTrainingWords = async () => {
    const words = await wordService.getTrainWords(
      user.id, wordLang.value, transLang.value, size
    )
    if (words.length === 0)
      dispatch(setErrorMessage('No words were found.'))
    setWords(words)
  }

  return (
    <Container>
      <Table>
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell>
              Word
            </Table.HeaderCell>
            <Table.HeaderCell>
              Translation
            </Table.HeaderCell>
            <Table.HeaderCell>
              Language
            </Table.HeaderCell>
            <Table.HeaderCell>
              Score
            </Table.HeaderCell>
          </Table.Row>
        </Table.Header>
        <Table.Body>
          { words.map(word =>
            <Table.Row key={word.id}>
              <Table.Cell>
                {word.word}
              </Table.Cell>
              <Table.Cell>
                {word.translation}
              </Table.Cell>
              <Table.Cell>
                {word.lang_word} / {word.lang_translation}
              </Table.Cell>
              <Table.Cell>
                {word.score}
              </Table.Cell>
            </Table.Row>          
          ) }
        </Table.Body>
      </Table> 
    </Container>
  )
}