#!/usr/bin/env python3
"""PDFから特典データのテキストを抽出するスクリプト"""

import sys
import argparse
import re
from pathlib import Path
from collections import Counter

import pdfplumber


def extract_text_from_pdf(pdf_path: str) -> list[str]:
    """PDFファイルからページごとのテキストを抽出する。

    Args:
        pdf_path: PDFファイルのパス

    Returns:
        ページごとのテキストリスト（空ページは空文字列）
    """
    pages_text = []
    with pdfplumber.open(pdf_path) as pdf:
        for page in pdf.pages:
            text = page.extract_text() or ''
            pages_text.append(text)
    return pages_text


def remove_noise(pages_text: list[str]) -> str:
    """ヘッダー・フッター・ページ番号などのノイズを除去してテキストを結合する。

    Args:
        pages_text: ページごとのテキストリスト

    Returns:
        クリーニング済みのテキスト（ページ間は空行で区切る）
    """
    # ページ番号パターン
    PAGE_NUM_PATTERNS = [
        re.compile(r'^\d+$'),               # 数字のみ（例: 1, 2, 3）
        re.compile(r'^[-−]\s*\d+\s*[-−]$'), # ダッシュ付き（例: - 1 -）
        re.compile(r'^第\d+ページ$'),         # 日本語形式（例: 第1ページ）
    ]

    # 非空ページのみ対象
    non_empty_pages = [p for p in pages_text if p.strip()]
    total_pages = len(non_empty_pages)

    # 全ページに共通する行を検出（ヘッダー・フッターとみなす）
    repeated_lines: set[str] = set()
    if total_pages >= 2:
        all_lines = [
            line.strip()
            for page in non_empty_pages
            for line in page.split('\n')
            if line.strip()
        ]
        threshold = max(2, total_pages * 0.5)
        line_count = Counter(all_lines)
        repeated_lines = {
            line for line, count in line_count.items() if count >= threshold
        }

    cleaned_pages = []
    for page in pages_text:
        if not page.strip():
            continue
        cleaned_lines = []
        for line in page.split('\n'):
            stripped = line.strip()
            # ページ番号をスキップ
            if any(p.match(stripped) for p in PAGE_NUM_PATTERNS):
                continue
            # 繰り返し行（ヘッダー・フッター）をスキップ
            if stripped in repeated_lines:
                continue
            cleaned_lines.append(line)
        cleaned_pages.append('\n'.join(cleaned_lines))

    return '\n\n'.join(cleaned_pages)


def save_output(text: str, output_path: str) -> None:
    """テキストをファイルに保存する。

    Args:
        text: 保存するテキスト
        output_path: 出力ファイルパス
    """
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(text)


def main() -> None:
    """メイン処理"""
    parser = argparse.ArgumentParser(
        description='PDFから特典データのテキストを抽出し、Claude.ai用に整形します'
    )
    parser.add_argument('pdf_path', help='抽出するPDFファイルのパス')
    parser.add_argument(
        '--force',
        action='store_true',
        help='出力ファイルが既に存在する場合でも上書きする'
    )
    parser.add_argument(
        '-o', '--output',
        default='output.txt',
        help='出力ファイルパス（デフォルト: output.txt）'
    )
    args = parser.parse_args()

    pdf_path = Path(args.pdf_path)
    if not pdf_path.is_file():
        print(f'エラー: 有効なPDFファイルではありません: {args.pdf_path}', file=sys.stderr)
        sys.exit(1)

    output_path_obj = Path(args.output)
    if output_path_obj.exists() and not args.force:
        print(
            f'エラー: {args.output} は既に存在します。--force オプションで上書きできます。',
            file=sys.stderr
        )
        sys.exit(1)

    print(f'PDFを読み込み中: {args.pdf_path}')
    pages_text = extract_text_from_pdf(str(pdf_path))
    print(f'{len(pages_text)} ページを検出しました')

    cleaned_text = remove_noise(pages_text)
    save_output(cleaned_text, args.output)

    char_count = len(cleaned_text)
    print(f'テキストを保存しました: {args.output} （{char_count} 文字）')
    print()
    print('=' * 50)
    print('次のステップ')
    print('=' * 50)
    print(f'1. {args.output} の内容をコピーする')
    print('2. prompt_template.txt を開き、[採番開始ID] を現在の最大ID+1 に書き換える')
    print('   （最大IDの確認: SELECT MAX(BENEFIT_ID) FROM BENEFIT;）')
    print('3. prompt_template.txt の内容 + output.txt の内容を Claude.ai に貼り付ける')
    print('4. 生成された SQL を確認し、config/database/DML/TABLE/BENEFIT.SQL に追記する')
    print('5. 店舗特典がある場合: POST /admin/benefits/geocode でジオコーディングを実行する')


if __name__ == '__main__':
    main()
