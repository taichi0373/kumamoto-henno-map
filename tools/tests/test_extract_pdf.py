import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))

from extract_pdf import remove_noise


def test_ページ番号のみの行を除去する():
    """数字だけの行（ページ番号）が除去されること"""
    pages = ['特典内容\nバス割引\n1', '詳細情報\nタクシー割引\n2']
    result = remove_noise(pages)
    lines = [line.strip() for line in result.split('\n') if line.strip()]
    assert '1' not in lines
    assert '2' not in lines
    assert 'バス割引' in result
    assert 'タクシー割引' in result


def test_ダッシュ付きページ番号を除去する():
    """「- 1 -」形式のページ番号が除去されること"""
    pages = ['特典内容\nバス割引\n- 1 -', '詳細\nタクシー\n- 2 -']
    result = remove_noise(pages)
    lines = [line.strip() for line in result.split('\n') if line.strip()]
    assert '- 1 -' not in lines
    assert '- 2 -' not in lines


def test_全ページ共通のヘッダーを除去する():
    """全ページに共通する行（ヘッダー・フッター）が除去されること"""
    header = '熊本県自主返納特典一覧'
    pages = [
        f'{header}\n特典A\n内容A',
        f'{header}\n特典B\n内容B',
        f'{header}\n特典C\n内容C',
    ]
    result = remove_noise(pages)
    assert header not in result
    assert '特典A' in result
    assert '内容A' in result


def test_ユニークなコンテンツは保持される():
    """各ページ固有のコンテンツは除去されないこと"""
    pages = ['特典A\nバス半額割引\n1', '特典B\nタクシー10%割引\n2']
    result = remove_noise(pages)
    assert '特典A' in result
    assert 'バス半額割引' in result
    assert '特典B' in result
    assert 'タクシー10%割引' in result


def test_1ページのみの場合はヘッダー除去しない():
    """1ページしかない場合、繰り返し行の判定を行わないこと"""
    pages = ['特典A\nバス半額割引\n問合せ: 096-XXX-XXXX']
    result = remove_noise(pages)
    assert '特典A' in result
    assert 'バス半額割引' in result
    assert '問合せ: 096-XXX-XXXX' in result


def test_空ページを無視する():
    """空のページがあってもエラーにならないこと"""
    pages = ['特典A\n内容A', '', '特典B\n内容B']
    result = remove_noise(pages)
    assert '特典A' in result
    assert '特典B' in result


def test_extract_text_from_pdf_がページテキストのリストを返す():
    """extract_text_from_pdfがページごとのテキストリストを返すこと"""
    from unittest.mock import patch, MagicMock
    from extract_pdf import extract_text_from_pdf

    mock_page1 = MagicMock()
    mock_page1.extract_text.return_value = 'ページ1のテキスト'
    mock_page2 = MagicMock()
    mock_page2.extract_text.return_value = 'ページ2のテキスト'
    mock_page3 = MagicMock()
    mock_page3.extract_text.return_value = None  # 空ページ

    mock_pdf = MagicMock()
    mock_pdf.__enter__ = MagicMock(return_value=mock_pdf)
    mock_pdf.__exit__ = MagicMock(return_value=False)
    mock_pdf.pages = [mock_page1, mock_page2, mock_page3]

    with patch('extract_pdf.pdfplumber.open', return_value=mock_pdf):
        result = extract_text_from_pdf('dummy.pdf')

    assert result == ['ページ1のテキスト', 'ページ2のテキスト', '']
    assert len(result) == 3
