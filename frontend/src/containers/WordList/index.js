import React, { useState } from 'react'
import { useSelector } from 'react-redux'
import {
  Container, Segment, Table, Button
} from 'semantic-ui-react'

import { WordInsertion } from './WordInsertion'
import { WordPair } from './WordPair'


export const languageOptions = [
  {
    key: 'russian',
    text: 'russian',
    value: 'russian'
  },
  {
    key: 'english',
    text: 'english',
    value: 'english'
  },
  {
    key: 'finnish',
    text: 'finnish',
    value: 'finnish'
  }
]

export const WordList = () => {
  const [formVisibilty, setVisibility] = useState(false)

  const words = useSelector(state => state.words)
  words.sort((a, b) => (a.score / (a.tries + 1)) - (b.score / (b.tries + 1)))

  const showForm = () =>
    setVisibility(!formVisibilty)

  return (
    <Container>
      <Segment>
        { formVisibilty && <><WordInsertion /><br /></> }
        <Button fluid onClick={showForm}>
          { formVisibilty
            ? 'Hide'
            : 'Add words' }
        </Button>
      </Segment>
      <Table>
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell>Word</Table.HeaderCell>
            <Table.HeaderCell>Translation</Table.HeaderCell>
            <Table.HeaderCell>Languages</Table.HeaderCell>
            <Table.HeaderCell>Score</Table.HeaderCell>
            <Table.HeaderCell />
            <Table.HeaderCell />
          </Table.Row>
        </Table.Header>
        <Table.Body>
          { words.map(word => (
            <WordPair key={word.id} wordPair={word} />
          )) }
        </Table.Body>
      </Table>
    </Container>
  )
}
