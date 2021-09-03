import React from 'react'
import PropTypes from 'prop-types'
import { Button, Dropdown, Table } from 'semantic-ui-react'

import { useField } from 'utils/useField'
import { languageOptions } from '../WordList'


export const WordSearchBar = ({ getLanguageWords }) => {
  const [wordLang] = useField('dropdown', '', false)
  const [transLang] = useField('dropdown', '', false)

  return (
    <Table>
      <Table.Header>
        <Table.Row>
          <Table.HeaderCell>
            Translation language
          </Table.HeaderCell>
          <Table.HeaderCell>
            Training language
          </Table.HeaderCell>
          <Table.HeaderCell />
        </Table.Row>

        <Table.Row>
          <Table.HeaderCell>
            <Dropdown fluid {...wordLang} placeholder='Select Language' selection options={languageOptions} />
          </Table.HeaderCell>
          <Table.HeaderCell>
            <Dropdown fluid {...transLang} placeholder='Select Language' selection options={languageOptions} />
          </Table.HeaderCell>
          <Table.HeaderCell>
            <Button
              positive
              onClick={() => getLanguageWords(wordLang.value, transLang.value)}
              content='Start' />
          </Table.HeaderCell>
        </Table.Row>
      </Table.Header>
    </Table>
  )
}

WordSearchBar.propTypes = {
  getLanguageWords: PropTypes.func.isRequired
}